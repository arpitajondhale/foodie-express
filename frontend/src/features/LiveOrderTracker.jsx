import { useState, useEffect } from 'react';
import { api } from '../services/api';
import InvoiceModal from '../components/ui/InvoiceModal';
import {
  CheckCircle2,
  Clock,
  ChefHat,
  Package,
  Bike,
  Home,
  FileText,
  Star,
  Phone,
  RefreshCw,
  Send,
} from 'lucide-react';

export default function LiveOrderTracker({ orderId, initialOrder, onBackToStore }) {
  const [order, setOrder] = useState(initialOrder);
  const [loading, setLoading] = useState(!initialOrder);
  const [invoiceOpen, setInvoiceOpen] = useState(false);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSubmitted, setReviewSubmitted] = useState(false);

  // Poll order status every 4 seconds to reflect WebSocket/kitchen updates
  useEffect(() => {
    loadOrder();
    const interval = setInterval(loadOrder, 4000);
    return () => clearInterval(interval);
  }, [orderId]);

  const loadOrder = async () => {
    if (!orderId && !initialOrder?.id) return;
    const id = orderId || initialOrder.id;
    try {
      const data = await api.getOrderById(id);
      setOrder(data);
    } catch {
      // Ignored on transient error
    } finally {
      setLoading(false);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!order) return;
    try {
      await api.addReview({
        orderId: order.id,
        restaurantId: order.restaurantId,
        rating: reviewRating,
        comment: reviewComment,
      });
      setReviewSubmitted(true);
    } catch {
      // Ignored
    }
  };

  if (loading || !order) {
    return (
      <div className="tracker-loading">
        <RefreshCw className="spinner" size={32} />
        <p>Loading live tracking...</p>
      </div>
    );
  }

  // Stages of order lifecycle
  const stages = [
    { key: 'PLACED', label: 'Order Placed', icon: Clock, desc: 'Received & routed to kitchen' },
    { key: 'CONFIRMED', label: 'Confirmed', icon: CheckCircle2, desc: 'Kitchen accepted order' },
    { key: 'PREPARING', label: 'Preparing', icon: ChefHat, desc: 'Chef cooking with health macros' },
    { key: 'READY_FOR_PICKUP', label: 'Ready', icon: Package, desc: 'Packed and waiting for pickup' },
    { key: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', icon: Bike, desc: 'Rider is on the way' },
    { key: 'DELIVERED', label: 'Delivered', icon: Home, desc: 'Enjoy your healthy meal!' },
  ];

  const currentIdx = stages.findIndex((s) => s.key === order.orderStatus);
  const isDelivered = order.orderStatus === 'DELIVERED';
  const isCancelled = order.orderStatus === 'CANCELLED';

  // Calculate rider map progress percentage
  const riderProgress = isDelivered
    ? 100
    : order.orderStatus === 'OUT_FOR_DELIVERY'
    ? 75
    : order.orderStatus === 'READY_FOR_PICKUP'
    ? 50
    : order.orderStatus === 'PREPARING'
    ? 30
    : 10;

  return (
    <div className="order-tracker-page">
      {/* Top Bar */}
      <div className="tracker-top-bar">
        <div>
          <h2>Tracking Order: {order.orderNumber}</h2>
          <span className="order-kitchen-tag">From {order.restaurantName}</span>
        </div>
        <div className="top-bar-buttons">
          <button className="btn-secondary" onClick={() => setInvoiceOpen(true)}>
            <FileText size={16} /> Tax Invoice
          </button>
          <button className="btn-secondary" onClick={onBackToStore}>
            Order More Dishes
          </button>
        </div>
      </div>

      {isCancelled ? (
        <div className="cancelled-banner">
          <h3>Order Cancelled</h3>
          <p>{order.cancellationReason || 'Order was cancelled by the kitchen.'}</p>
        </div>
      ) : (
        <div className="tracker-main-grid">
          {/* Left Column: Timeline & Map */}
          <div className="tracker-left">
            {/* Live Progress Timeline */}
            <div className="timeline-card">
              <div className="timeline-header">
                <h3>Live Order Lifecycle</h3>
                <span className="live-status-pill">{order.orderStatus.replace(/_/g, ' ')}</span>
              </div>

              <div className="stages-timeline">
                {stages.map((stage, idx) => {
                  const Icon = stage.icon;
                  const isCompleted = currentIdx >= idx;
                  const isCurrent = currentIdx === idx;
                  return (
                    <div
                      key={stage.key}
                      className={`timeline-step ${isCompleted ? 'completed' : ''} ${isCurrent ? 'current' : ''}`}
                    >
                      <div className="step-icon-col">
                        <div className="step-circle">
                          <Icon size={18} />
                        </div>
                        {idx < stages.length - 1 && <div className="step-line" />}
                      </div>
                      <div className="step-info">
                        <div className="step-title">{stage.label}</div>
                        <div className="step-desc">{stage.desc}</div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Live Simulated GPS Delivery Map */}
            <div className="rider-map-card">
              <div className="map-header">
                <Bike size={18} className="text-primary" />
                <h4>Live GPS Delivery Partner Tracker</h4>
                <span className="eta-badge">{order.estimatedDeliveryTime || '25-35 mins'}</span>
              </div>

              <div className="simulated-map-view">
                {/* Visual Map Route */}
                <div className="map-route-visual">
                  {/* Restaurant Node */}
                  <div className="route-node restaurant-node">
                    <ChefHat size={16} />
                    <span>Kitchen</span>
                  </div>

                  {/* Road Line with Moving Rider */}
                  <div className="route-track">
                    <div className="track-fill" style={{ width: `${riderProgress}%` }} />
                    <div className="rider-pin" style={{ left: `${riderProgress}%` }}>
                      <Bike size={20} className="rider-bike-icon" />
                    </div>
                  </div>

                  {/* Customer Home Node */}
                  <div className="route-node customer-node">
                    <Home size={16} />
                    <span>Delivery Address</span>
                  </div>
                </div>

                <div className="map-footer-info">
                  <div className="rider-info-row">
                    <div className="rider-avatar">
                      {order.deliveryPartnerName ? order.deliveryPartnerName[0] : 'R'}
                    </div>
                    <div>
                      <div className="rider-name">
                        {order.deliveryPartnerName || 'Rajesh Kumar (Partner Assigned)'}
                      </div>
                      <div className="rider-rating">★ 4.9 • Electric EV Fleet • Vaccinated</div>
                    </div>
                    <button className="call-rider-btn" onClick={() => alert('Dialing Rider: +91 9876543212')}>
                      <Phone size={14} /> Call Rider
                    </button>
                  </div>
                </div>
              </div>
            </div>

            {/* Post-Delivery Rating Form */}
            {isDelivered && (
              <div className="review-card">
                <h3>Rate Your Meal & Service</h3>
                {reviewSubmitted ? (
                  <div className="review-thankyou">
                    <CheckCircle2 size={24} className="text-success" />
                    <p>Thank you for rating! Your review helps others eat healthily.</p>
                  </div>
                ) : (
                  <form onSubmit={handleReviewSubmit} className="review-form">
                    <div className="star-rating-selector">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <button
                          key={star}
                          type="button"
                          className={`star-btn ${reviewRating >= star ? 'active' : ''}`}
                          onClick={() => setReviewRating(star)}
                        >
                          <Star size={24} fill={reviewRating >= star ? '#f59e0b' : 'none'} />
                        </button>
                      ))}
                    </div>
                    <textarea
                      rows={2}
                      placeholder="Share your experience (taste, hygiene, macro accuracy)..."
                      value={reviewComment}
                      onChange={(e) => setReviewComment(e.target.value)}
                    />
                    <button type="submit" className="submit-review-btn">
                      <Send size={14} /> Submit Feedback
                    </button>
                  </form>
                )}
              </div>
            )}
          </div>

          {/* Right Column: Order Summary */}
          <div className="tracker-right">
            <div className="order-recap-card">
              <h4>Order Items ({order.items?.length})</h4>
              <div className="recap-items-list">
                {order.items?.map((item) => (
                  <div key={item.id} className="recap-item-row">
                    <div>
                      <span className="recap-item-name">{item.foodName} × {item.quantity}</span>
                      {item.customizationDetails && (
                        <span className="recap-item-cust">{item.customizationDetails}</span>
                      )}
                    </div>
                    <span className="recap-item-sub">₹{item.subtotal}</span>
                  </div>
                ))}
              </div>

              <div className="bill-divider" />

              <div className="recap-totals">
                <div className="recap-row">
                  <span>Item Total</span>
                  <span>₹{order.itemTotal}</span>
                </div>
                <div className="recap-row">
                  <span>Packaging & Delivery</span>
                  <span>₹{(order.packagingFee + order.deliveryFee).toFixed(2)}</span>
                </div>
                <div className="recap-row">
                  <span>GST (5%)</span>
                  <span>₹{order.gstAmount}</span>
                </div>
                {order.discountAmount > 0 && (
                  <div className="recap-row discount">
                    <span>Discount</span>
                    <span>-₹{order.discountAmount}</span>
                  </div>
                )}
                <div className="recap-row grand-total">
                  <span>Grand Total</span>
                  <span>₹{order.grandTotal}</span>
                </div>
              </div>

              <div className="delivery-address-card">
                <div className="addr-header">
                  <Home size={14} />
                  <span>Delivering to</span>
                </div>
                <p className="addr-val">{order.deliveryAddressSnapshot}</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Invoice Modal */}
      {invoiceOpen && (
        <InvoiceModal order={order} onClose={() => setInvoiceOpen(false)} />
      )}
    </div>
  );
}