import { useState } from 'react';
import { X, Check } from 'lucide-react';
import { useCart } from '../../context/CartContext';

export default function CustomizationModal({ foodItem, onClose }) {
  const { addToCart } = useCart();
  const [selectedOptions, setSelectedOptions] = useState({});

  if (!foodItem) return null;

  const handleOptionSelect = (groupName, option) => {
    setSelectedOptions((prev) => ({
      ...prev,
      [groupName]: option,
    }));
  };

  const calculatedExtra = Object.values(selectedOptions).reduce(
    (sum, opt) => sum + (opt?.extraPrice || 0),
    0
  );

  const basePrice = foodItem.discountPrice || foodItem.price;
  const finalPrice = basePrice + calculatedExtra;

  const handleAdd = () => {
    const optionsArray = Object.values(selectedOptions).filter(Boolean);
    addToCart(foodItem, 1, optionsArray);
    onClose();
  };

  return (
    <div className="modal-backdrop">
      <div className="modal-container customization-modal">
        <div className="modal-header">
          <div>
            <h3>Customise {foodItem.name}</h3>
            <span className="base-price-tag">Base Price: ₹{basePrice}</span>
          </div>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="customization-body">
          {foodItem.customizations?.map((group) => (
            <div key={group.id} className="customization-group">
              <div className="group-header">
                <span className="group-name">{group.groupName}</span>
                {group.required && <span className="required-badge">Required</span>}
              </div>

              <div className="options-list">
                {group.options?.map((opt) => {
                  const isSelected = selectedOptions[group.groupName]?.id === opt.id;
                  return (
                    <label
                      key={opt.id}
                      className={`option-row ${isSelected ? 'selected' : ''}`}
                      onClick={() => handleOptionSelect(group.groupName, opt)}
                    >
                      <div className="radio-circle">{isSelected && <Check size={12} />}</div>
                      <span className="option-name">{opt.name}</span>
                      <span className="option-extra">
                        {opt.extraPrice > 0 ? `+₹${opt.extraPrice}` : 'Free'}
                      </span>
                    </label>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        <div className="modal-footer">
          <div className="final-price-box">
            <span className="total-label">Total Price:</span>
            <span className="total-val">₹{finalPrice}</span>
          </div>
          <button className="btn-primary" onClick={handleAdd}>
            Add Item to Cart
          </button>
        </div>
      </div>
    </div>
  );
}