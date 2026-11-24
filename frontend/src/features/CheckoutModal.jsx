import { useState, useEffect } from 'react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import {
  X,
  MapPin,
  CreditCard,
  QrCode,
  Banknote,
  CheckCircle2,
  Lock,
  ArrowRight,
} from 'lucide-react';

export default function CheckoutModal({ isOpen, onClose, onOrderPlaced }) {
  const { items, restaurant, grandTotal, appliedCoupon, clearCart } = useCart();
  const { user } = useAuth();

  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [customAddress, setCustomAddress] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('UPI');
  const [upiId, setUpiId] = useState('aarav@oksbi');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen && user) {
      api.getAddresses()
        .then((data) => {
          setAddresses(data);
          const def = data.find((a) => a.defaultAddress) || data[0];
          if (def) setSelectedAddressId(def.id);
        })
        .catch(() => {});
    }
  }, [isOpen, user]);

  if (!isOpen) return null;

  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Build order items
      const orderPayloadItems = items.map((i) => ({
        foodItemId: i.foodItem.id,
        quantity: i.quantity,
        selectedOptionIds: (i.selectedOptions || []).map((o) => o.id),
      }));

      let addressSnapshot = customAddress.trim();
      if (!addressSnapshot && selectedAddressId) {
        const addr = addresses.find((a) => a.id === selectedAddressId);
        if (addr) {
          addressSnapshot = `${addr.label}: ${addr.street}, ${addr.city} - ${addr.pincode}`;
        }
      }
      if (!addressSnapshot) {
        addressSnapshot = 'Flat 402, Palm Meadows, Whitefield, Bengaluru - 560066';
      }

      const payload = {
        restaurantId: restaurant.id,
        items: orderPayloadItems,
        addressId: selectedAddressId,
        deliveryAddressSnapshot: addressSnapshot,
        paymentMethod,
        couponCode: appliedCoupon?.code || null,
      };

      const placedOrder = await api.createOrder(payload);
      clearCart();
      onClose();
      onOrderPlaced(placedOrder);
    } catch (err) {
      setError(err.message || 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal-container checkout-modal">
        <div className="modal-header">
          <div>
            <h3>Confirm & Pay</h3>
            <span className="modal-subtitle">Total Payable: ₹{grandTotal.toFixed(2)}</span>
          </div>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handlePlaceOrder} className="checkout-form">
          {error && <div className="error-alert">{error}</div>}

          {/* 1. Delivery Address Selection */}
          <div className="checkout-section">
            <div className="section-title-row">
              <MapPin size={18} />
              <h4>1. Select Delivery Address</h4>
            </div>

            {addresses.length > 0 && (
              <div className="address-options-grid">
                {addresses.map((addr) => {
                  const isSelected = selectedAddressId === addr.id;
                  return (
                    <div
                      key={addr.id}
                      className={`address-card-select ${isSelected ? 'selected' : ''}`}
                      onClick={() => {
                        setSelectedAddressId(addr.id);
                        setCustomAddress('');
                      }}
                    >
                      <div className="addr-label-row">
                        <strong>{addr.label}</strong>
                        {addr.defaultAddress && <span className="def-tag">Default</span>}
                      </div>
                      <p className="addr-text">
                        {addr.street}, {addr.city} - {addr.pincode}
                      </p>
                    </div>
                  );
                })}
              </div>
            )}

            <div className="custom-address-input">
              <label>Or Enter Custom Delivery Address:</label>
              <textarea
                rows={2}
                placeholder="Apartment, building, street, landmark, city, pincode..."
                value={customAddress}
                onChange={(e) => {
                  setCustomAddress(e.target.value);
                  setSelectedAddressId(null);
                }}
              />
            </div>
          </div>

          {/* 2. Payment Method Selector */}
          <div className="checkout-section">
            <div className="section-title-row">
              <CreditCard size={18} />
              <h4>2. Select Payment Option</h4>
            </div>

            <div className="payment-methods-grid">
              {/* UPI */}
              <div
                className={`payment-option ${paymentMethod === 'UPI' ? 'selected' : ''}`}
                onClick={() => setPaymentMethod('UPI')}
              >
                <div className="pay-radio">
                  <div className="radio-dot" />
                </div>
                <QrCode size={20} className="pay-icon" />
                <div className="pay-details">
                  <span className="pay-name">Instant UPI Payment</span>
                  <span className="pay-desc">GPay, PhonePe, Paytm, BHIM</span>
                </div>
              </div>

              {/* CARD */}
              <div
                className={`payment-option ${paymentMethod === 'CARD' ? 'selected' : ''}`}
                onClick={() => setPaymentMethod('CARD')}
              >
                <div className="pay-radio">
                  <div className="radio-dot" />
                </div>
                <CreditCard size={20} className="pay-icon" />
                <div className="pay-details">
                  <span className="pay-name">Credit / Debit Card</span>
                  <span className="pay-desc">Visa, Mastercard, RuPay</span>
                </div>
              </div>

              {/* CASH ON DELIVERY */}
              <div
                className={`payment-option ${paymentMethod === 'COD' ? 'selected' : ''}`}
                onClick={() => setPaymentMethod('COD')}
              >
                <div className="pay-radio">
                  <div className="radio-dot" />
                </div>
                <Banknote size={20} className="pay-icon" />
                <div className="pay-details">
                  <span className="pay-name">Cash on Delivery</span>
                  <span className="pay-desc">Pay upon meal arrival</span>
                </div>
              </div>
            </div>

            {/* Dynamic Payment Details Mockup */}
            {paymentMethod === 'UPI' && (
              <div className="upi-details-card">
                <div className="upi-qr-mock">
                  <div className="qr-box">
                    <QrCode size={70} />
                  </div>
                  <div className="qr-info">
                    <span className="qr-title">Scan QR code with any UPI app</span>
                    <span className="qr-timer">Simulated Auto-Verification</span>
                  </div>
                </div>
                <div className="upi-vpa-input">
                  <label>Or enter UPI VPA ID:</label>
                  <input
                    type="text"
                    value={upiId}
                    onChange={(e) => setUpiId(e.target.value)}
                    placeholder="username@bank"
                  />
                </div>
              </div>
            )}

            {paymentMethod === 'CARD' && (
              <div className="card-mock-form">
                <input type="text" placeholder="Card Number (4532 •••• •••• 8821)" defaultValue="4532 9182 3412 8821" />
                <div className="card-row">
                  <input type="text" placeholder="MM/YY" defaultValue="12/28" />
                  <input type="password" placeholder="CVV" defaultValue="734" />
                </div>
              </div>
            )}
          </div>

          {/* Security & Action */}
          <div className="checkout-footer">
            <div className="secure-badge">
              <Lock size={14} /> 256-Bit Bank Grade SSL Encrypted
            </div>
            <button type="submit" className="place-order-btn" disabled={loading}>
              {loading ? 'Processing Order...' : `Pay ₹${grandTotal.toFixed(2)} & Place Order`}
              <ArrowRight size={18} />
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}