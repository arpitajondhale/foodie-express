import { useState } from 'react';
import { useCart } from '../../context/CartContext';
import { useHealthProfile } from '../../context/HealthProfileContext';
import { Plus, Minus, ShieldCheck, Flame, HeartHandshake } from 'lucide-react';

export default function DishCard({ dish, onOpenCustomization }) {
  const { items, addToCart, updateQuantity } = useCart();
  const { profile } = useHealthProfile();

  // Find if dish is in cart
  const cartItem = items.find((i) => i.foodItem.id === dish.id);
  const qty = cartItem ? cartItem.quantity : 0;

  const nut = dish.nutritionalInfo;
  const isCustomisable = dish.customizations && dish.customizations.length > 0;

  // Determine if safe for current user profile
  const isSafeForUser = (() => {
    if (!profile || (!profile.conditions?.length && !profile.allergies?.length)) return false;
    if (!nut) return false;

    // Check allergy
    if (profile.allergies?.some((a) => nut.allergens?.includes(a))) return false;

    // Check condition
    if (profile.conditions?.some((c) => nut.contraindicatedConditions?.includes(c))) return false;

    // Check limits
    if (profile.conditions?.includes('DIABETES') && nut.sugarGrams > (profile.maxSugarGramsPerMeal || 15)) return false;
    if (profile.conditions?.includes('HYPERTENSION') && nut.sodiumMg > (profile.maxSodiumMgPerMeal || 600)) return false;

    return true;
  })();

  const handleAddClick = () => {
    if (isCustomisable) {
      onOpenCustomization(dish);
    } else {
      addToCart(dish, 1, []);
    }
  };

  return (
    <div className={`dish-card ${!dish.available ? 'sold-out' : ''}`}>
      <div className="dish-main">
        {/* Left Info Column */}
        <div className="dish-details">
          {/* Diet & Tagline */}
          <div className="dish-badge-row">
            <span className={`diet-icon ${dish.dietType?.toLowerCase()}`}>
              <span className="diet-dot" />
            </span>
            {dish.dietType && <span className="diet-name">{dish.dietType}</span>}
            {isSafeForUser && (
              <span className="safe-pill" title="Matches your health & allergy profile">
                <ShieldCheck size={13} /> Safe for You
              </span>
            )}
            {dish.spiceLevel && (
              <span className={`spice-pill spice-${dish.spiceLevel.toLowerCase()}`}>
                {dish.spiceLevel === 'HOT' ? '🌶️ Hot' : dish.spiceLevel === 'MEDIUM' ? '🌶️ Med' : '🌱 Mild'}
              </span>
            )}
          </div>

          <h4 className="dish-name">{dish.name}</h4>

          <div className="dish-pricing">
            <span className="current-price">₹{dish.discountPrice || dish.price}</span>
            {dish.discountPrice && (
              <span className="original-price">₹{dish.price}</span>
            )}
            {dish.averageRating && (
              <span className="rating-pill">★ {dish.averageRating}</span>
            )}
          </div>

          <p className="dish-desc">{dish.description}</p>

          {/* Nutritional Macro Tags */}
          {nut && (
            <div className="macro-bar">
              <span className="macro-chip">
                <Flame size={12} className="text-orange" /> {nut.calories} kcal
              </span>
              <span className="macro-chip">💪 {nut.proteinGrams}g Protein</span>
              <span className="macro-chip">🍬 {nut.sugarGrams}g Sugar</span>
              <span className="macro-chip">🧂 {nut.sodiumMg}mg Sodium</span>
              {nut.glycemicIndex && (
                <span className={`macro-chip gi-${nut.glycemicIndex.toLowerCase()}`}>
                  {nut.glycemicIndex} GI
                </span>
              )}
            </div>
          )}

          {/* Suitable Condition Badges */}
          {nut?.suitableForConditions?.length > 0 && (
            <div className="suitability-tags">
              {nut.suitableForConditions.slice(0, 3).map((cond) => (
                <span key={cond} className="health-tag">
                  ✓ {cond.replace(/_/g, ' ')}
                </span>
              ))}
            </div>
          )}
        </div>

        {/* Right Image & Action Column */}
        <div className="dish-media">
          <div className="dish-img-wrap">
            <img
              src={dish.imageUrl || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300'}
              alt={dish.name}
              className="dish-img"
              loading="lazy"
            />
            {!dish.available && <div className="sold-out-overlay">Sold Out</div>}
          </div>

          {/* Add / Stepper Button */}
          <div className="dish-action-container">
            {qty > 0 ? (
              <div className="stepper-btn">
                <button
                  type="button"
                  onClick={() => updateQuantity(cartItem.uniqueKey, -1)}
                  aria-label="Decrease quantity"
                >
                  <Minus size={14} />
                </button>
                <span className="stepper-qty">{qty}</span>
                <button
                  type="button"
                  onClick={() => updateQuantity(cartItem.uniqueKey, 1)}
                  aria-label="Increase quantity"
                >
                  <Plus size={14} />
                </button>
              </div>
            ) : (
              <button
                type="button"
                className="add-dish-btn"
                disabled={!dish.available}
                onClick={handleAddClick}
              >
                ADD
              </button>
            )}
            {isCustomisable && <span className="customise-subtext">Customisable</span>}
          </div>
        </div>
      </div>
    </div>
  );
}