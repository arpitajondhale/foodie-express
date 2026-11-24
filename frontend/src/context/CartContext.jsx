import { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from './AuthContext';

const CartContext = createContext();

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [items, setItems] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('foodie_cart_items') || '[]');
    } catch {
      return [];
    }
  });

  const [restaurant, setRestaurant] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('foodie_cart_restaurant') || 'null');
    } catch {
      return null;
    }
  });

  const [appliedCoupon, setAppliedCoupon] = useState(null);
  const [guardrailWarning, setGuardrailWarning] = useState(null);

  useEffect(() => {
    localStorage.setItem('foodie_cart_items', JSON.stringify(items));
    localStorage.setItem('foodie_cart_restaurant', JSON.stringify(restaurant));
  }, [items, restaurant]);

  // Check health guardrail before adding
  const addToCart = async (foodItem, quantity = 1, selectedOptions = [], bypassGuardrail = false) => {
    // Check if adding from different restaurant
    if (restaurant && restaurant.id !== foodItem.restaurantId) {
      if (!window.confirm(`Your cart already contains dishes from ${restaurant.name}. Would you like to reset your cart to add items from ${foodItem.restaurantName}?`)) {
        return false;
      }
      setItems([]);
      setAppliedCoupon(null);
    }

    // Set restaurant
    if (!restaurant || restaurant.id !== foodItem.restaurantId) {
      setRestaurant({ id: foodItem.restaurantId, name: foodItem.restaurantName });
    }

    // Evaluate health guardrail if not bypassed
    if (!bypassGuardrail && user && user.role === 'ROLE_CUSTOMER') {
      try {
        const evalRes = await api.evaluateCartItem(foodItem.id, user.id);
        if (evalRes && evalRes.hasWarning && evalRes.warningReasons?.length > 0) {
          setGuardrailWarning({
            foodItem,
            quantity,
            selectedOptions,
            warningReasons: evalRes.warningReasons,
            suggestedAlternatives: evalRes.suggestedAlternatives || [],
          });
          return false;
        }
      } catch {
        // Proceed on eval failure
      }
    }

    // Construct unique key based on item ID and customizations
    const optIds = (selectedOptions || []).map((o) => o.id).sort().join('-');
    const uniqueKey = `${foodItem.id}_${optIds}`;

    setItems((prev) => {
      const idx = prev.findIndex((i) => i.uniqueKey === uniqueKey);
      if (idx >= 0) {
        const next = [...prev];
        next[idx].quantity += quantity;
        return next;
      }
      return [...prev, { uniqueKey, foodItem, quantity, selectedOptions }];
    });

    return true;
  };

  const updateQuantity = (uniqueKey, delta) => {
    setItems((prev) => {
      return prev
        .map((i) => {
          if (i.uniqueKey === uniqueKey) {
            const nextQty = i.quantity + delta;
            return nextQty > 0 ? { ...i, quantity: nextQty } : null;
          }
          return i;
        })
        .filter(Boolean);
    });
  };

  const removeFromCart = (uniqueKey) => {
    setItems((prev) => prev.filter((i) => i.uniqueKey !== uniqueKey));
  };

  const clearCart = () => {
    setItems([]);
    setRestaurant(null);
    setAppliedCoupon(null);
    setGuardrailWarning(null);
  };

  const dismissGuardrail = () => setGuardrailWarning(null);

  const proceedWithWarning = () => {
    if (guardrailWarning) {
      const { foodItem, quantity, selectedOptions } = guardrailWarning;
      setGuardrailWarning(null);
      addToCart(foodItem, quantity, selectedOptions, true);
    }
  };

  const applyCoupon = async (code) => {
    const res = await api.validateCoupon(code, itemTotal);
    setAppliedCoupon({
      code: res.code,
      discountAmount: res.discountAmount,
    });
    return res;
  };

  const removeCoupon = () => setAppliedCoupon(null);

  // Price calculations
  const itemTotal = items.reduce((sum, item) => {
    const basePrice = item.foodItem.discountPrice || item.foodItem.price;
    const extraPrice = (item.selectedOptions || []).reduce((s, o) => s + (o.extraPrice || 0), 0);
    return sum + (basePrice + extraPrice) * item.quantity;
  }, 0);

  const totalQuantity = items.reduce((sum, item) => sum + item.quantity, 0);
  const packagingFee = items.length > 0 ? 25.0 : 0.0;
  const deliveryFee = items.length > 0 ? (itemTotal >= 500.0 ? 0.0 : 35.0) : 0.0;
  const gstAmount = Math.round(itemTotal * 0.05 * 100.0) / 100.0; // 5% GST
  const discountAmount = appliedCoupon ? Math.min(appliedCoupon.discountAmount, itemTotal) : 0.0;
  const grandTotal = Math.max(0, itemTotal + packagingFee + deliveryFee + gstAmount - discountAmount);

  return (
    <CartContext.Provider
      value={{
        items,
        restaurant,
        totalQuantity,
        itemTotal,
        packagingFee,
        deliveryFee,
        gstAmount,
        discountAmount,
        grandTotal,
        appliedCoupon,
        guardrailWarning,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
        dismissGuardrail,
        proceedWithWarning,
        applyCoupon,
        removeCoupon,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);