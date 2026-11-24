import { useState } from 'react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import {
  X,
  Plus,
  Minus,
  Trash2,
  Tag,
  ArrowRight,
  ShieldCheck,
  ShoppingBag,
} from 'lucide-react';

export default function CartDrawer({ isOpen, onClose, onProceedToCheckout }) {
  const {
    items,
    restaurant,
    itemTotal,
    packagingFee,
    deliveryFee,
    gstAmount,
    discountAmount,
    grandTotal,
    appliedCoupon,
    updateQuantity,
    removeFromCart,
    clearCart,
    applyCoupon,
    removeCoupon,
  } = useCart();

  const { user } = useAuth();
  const [couponInput, setCouponInput] = useState('');
  const [couponError, setCouponError] = useState('');
  const [couponLoading, setCouponLoading] = useState(false);

  if (!isOpen) return null;

  const handleApplyCoupon = async (e) => {
    e.preventDefault();
    if (!couponInput.trim()) return;
    setCouponError('');
    setCouponLoading(true);
    try {
      await applyCoupon(couponInput.trim().toUpperCase());
      setCouponInput('');
    } catch (err) {
      setCouponError(err.message || 'Invalid coupon code');
    } finally {
      setCouponLoading(false);
    }
  };

  return (
    <div className="cart-drawer-backdrop" onClick={onClose}>
      <div className="cart-drawer" onClick={(e) => e.stopPropagation()}>
        {/* Drawer Header */}
        <div className="drawer-header">
          <div className="drawer-title-group">
            <ShoppingBag size={20} />
            <h3>Your Order</h3>
            {restaurant && <span className="drawer-restaurant-name">from {restaurant.name}</span>}
          </div>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {items.length === 0 ? (
          <div className="empty-cart-state">
            <span className="empty-cart-icon">🛒</span>
            <h4>Your cart is empty</h4>
            <p>Explore healthy, diabetic-safe and macro-balanced dishes to get started.</p>
            <button className="btn-primary" onClick={onClose}>
              Browse Kitchens
            </button>
          </div>
        ) : (
          <div className="drawer-body">
            {/* Items List */}
            <div className="cart-items-list">
              {items.map((item) => (
                <div key={item.uniqueKey} className="cart-item-row">
                  <div className="item-left">
                    <span className={`diet-icon ${item.foodItem.dietType?.toLowerCase()}`}>
                      <span className="diet-dot" />
                    </span>
                    <div className="item-text">
                      <div className="item-name">{item.foodItem.name}</div>
                      {item.selectedOptions?.length > 0 && (
                        <div className="item-customizations">
                          {item.selectedOptions.map((o) => o.name).join(', ')}
                        </div>
                      )}
                      <div className="item-price">
                        ₹{(item.foodItem.discountPrice || item.foodItem.price) +
                          item.selectedOptions.reduce((s, o) => s + (o.extraPrice || 0), 0)}
                      </div>
                    </div>
                  </div>

                  {/* Quantity Stepper */}
                  <div className="stepper-btn">
                    <button onClick={() => updateQuantity(item.uniqueKey, -1)}>
                      <Minus size={13} />
                    </button>
                    <span className="stepper-qty">{item.quantity}</span>
                    <button onClick={() => updateQuantity(item.uniqueKey, 1)}>
                      <Plus size={13} />
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Clear Cart Button */}
            <div className="clear-cart-row">
              <button className="text-danger-btn" onClick={clearCart}>
                <Trash2 size={14} /> Clear Cart
              </button>
            </div>

            {/* Coupons Section */}
            <div className="coupon-card">
              <div className="coupon-header">
                <Tag size={16} />
                <span>Apply Promo Coupon</span>
              </div>

              {appliedCoupon ? (
                <div className="applied-coupon-pill">
                  <div>
                    <span className="code-text">{appliedCoupon.code}</span>
                    <span className="discount-saved">Saved ₹{appliedCoupon.discountAmount}</span>
                  </div>
                  <button className="remove-coupon-btn" onClick={removeCoupon}>
                    Remove
                  </button>
                </div>
              ) : (
                <form onSubmit={handleApplyCoupon} className="coupon-form">
                  <input
                    type="text"
                    placeholder="Enter code (e.g. WELCOME50, HEALTHY20)"
                    value={couponInput}
                    onChange={(e) => setCouponInput(e.target.value)}
                  />
                  <button type="submit" disabled={couponLoading}>
                    {couponLoading ? '...' : 'Apply'}
                  </button>
                </form>
              )}
              {couponError && <span className="error-text">{couponError}</span>}

              {/* Quick Coupon Chips */}
              {!appliedCoupon && (
                <div className="quick-coupon-chips">
                  <span onClick={() => applyCoupon('WELCOME50')}>WELCOME50 (50% Off)</span>
                  <span onClick={() => applyCoupon('HEALTHY20')}>HEALTHY20 (20% Off)</span>
                  <span onClick={() => applyCoupon('DIABETICCARE')}>DIABETICCARE (Flat ₹75)</span>
                </div>
              )}
            </div>

            {/* Bill Summary Breakdown */}
            <div className="bill-summary-card">
              <h4>Bill Details</h4>
              <div className="bill-row">
                <span>Item Total</span>
                <span>₹{itemTotal.toFixed(2)}</span>
              </div>
              <div className="bill-row">
                <span>Delivery Fee {itemTotal >= 500 && <span className="free-tag">FREE</span>}</span>
                <span>{deliveryFee === 0 ? '₹0.00' : `₹${deliveryFee.toFixed(2)}`}</span>
              </div>
              <div className="bill-row">
                <span>Restaurant Packaging</span>
                <span>₹{packagingFee.toFixed(2)}</span>
              </div>
              <div className="bill-row">
                <span>Govt. GST (5%)</span>
                <span>₹{gstAmount.toFixed(2)}</span>
              </div>
              {discountAmount > 0 && (
                <div className="bill-row discount-row">
                  <span>Coupon Savings</span>
                  <span>-₹{discountAmount.toFixed(2)}</span>
                </div>
              )}
              <div className="bill-divider" />
              <div className="bill-row grand-total-row">
                <span>To Pay</span>
                <span>₹{grandTotal.toFixed(2)}</span>
              </div>
            </div>
          </div>
        )}

        {/* Drawer Footer */}
        {items.length > 0 && (
          <div className="drawer-footer">
            <div className="footer-total-box">
              <span className="total-title">Total</span>
              <span className="total-amt">₹{grandTotal.toFixed(2)}</span>
            </div>
            <button className="checkout-btn" onClick={onProceedToCheckout}>
              Proceed to Pay <ArrowRight size={18} />
            </button>
          </div>
        )}
      </div>
    </div>
  );
}