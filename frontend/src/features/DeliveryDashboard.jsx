import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import {
  Bike,
  MapPin,
  CheckCircle2,
  Navigation,
  Phone,
  Power,
  RefreshCw,
  Clock,
  DollarSign,
} from 'lucide-react';

export default function DeliveryDashboard() {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [isOnline, setIsOnline] = useState(true);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadQueue();
    const timer = setInterval(loadQueue, 5000);
    return () => clearInterval(timer);
  }, []);

  const loadQueue = async () => {
    try {
      const data = await api.getDeliveryQueue();
      setOrders(data);
    } catch {
      // Ignored
    } finally {
      setLoading(false);
    }
  };

  const handlePickUp = async (orderId) => {
    try {
      await api.assignRider(orderId);
      await loadQueue();
    } catch (err) {
      alert(err.message || 'Pickup failed');
    }
  };

  const handleDeliver = async (orderId) => {
    try {
      await api.updateOrderStatus(orderId, 'DELIVERED');
      await loadQueue();
    } catch (err) {
      alert(err.message || 'Delivery confirmation failed');
    }
  };

  return (
    <div className="portal-page">
      <div className="portal-header">
        <div className="portal-title-group">
          <div className="portal-icon delivery-icon">
            <Bike size={28} />
          </div>
          <div>
            <h2>Delivery Partner Fleet Portal</h2>
            <p>Logged in as: <strong>{user?.name || 'Rajesh Kumar'}</strong> (Electric Scooter Fleet)</p>
          </div>
        </div>

        {/* Online/Offline Status Toggle */}
        <div className="delivery-status-box">
          <button
            className={`online-toggle-btn ${isOnline ? 'online' : 'offline'}`}
            onClick={() => setIsOnline(!isOnline)}
          >
            <Power size={16} />
            <span>{isOnline ? 'Online & Available' : 'Offline'}</span>
          </button>
        </div>
      </div>

      <div className="portal-content">
        {/* Fleet KPI Banner */}
        <div className="analytics-kpi-grid delivery-kpi-grid">
          <div className="kpi-card">
            <span className="kpi-label">Active Queue</span>
            <span className="kpi-val">{orders.length}</span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Today's Deliveries</span>
            <span className="kpi-val">12</span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Delivery Earnings</span>
            <span className="kpi-val">₹840.00</span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Rider Rating</span>
            <span className="kpi-val">★ 4.95</span>
          </div>
        </div>

        <div className="queue-header-row">
          <h3>Orders Awaiting Pickup & In Transit</h3>
          <button className="btn-secondary" onClick={loadQueue}>
            <RefreshCw size={14} /> Refresh Deliveries
          </button>
        </div>

        {orders.length === 0 ? (
          <div className="portal-empty-card">
            <Bike size={40} className="empty-icon" />
            <h4>No active deliveries right now</h4>
            <p>New orders will appear here automatically when customer orders are placed.</p>
          </div>
        ) : (
          <div className="delivery-orders-grid">
            {orders.map((order) => {
              const isAssignedToMe = order.deliveryPartnerId === user?.id;
              const isReady = order.orderStatus === 'READY_FOR_PICKUP';
              const isOut = order.orderStatus === 'OUT_FOR_DELIVERY';

              return (
                <div key={order.id} className="delivery-ticket-card">
                  <div className="ticket-header">
                    <div>
                      <span className="ticket-number">{order.orderNumber}</span>
                      <span className="ticket-eta">Prep: {order.estimatedDeliveryTime}</span>
                    </div>
                    <span className={`status-tag ${order.orderStatus.toLowerCase()}`}>
                      {order.orderStatus.replace(/_/g, ' ')}
                    </span>
                  </div>

                  {/* Pick Up & Drop Address */}
                  <div className="route-addresses">
                    <div className="route-point">
                      <div className="point-dot pickup-dot" />
                      <div>
                        <span className="point-label">Pick up from:</span>
                        <strong>{order.restaurantName}</strong>
                      </div>
                    </div>
                    <div className="route-connector" />
                    <div className="route-point">
                      <div className="point-dot drop-dot" />
                      <div>
                        <span className="point-label">Deliver to:</span>
                        <strong>{order.customerName}</strong>
                        <p className="drop-address">{order.deliveryAddressSnapshot}</p>
                      </div>
                    </div>
                  </div>

                  {/* Items Summary */}
                  <div className="ticket-mini-items">
                    {order.items?.map((item) => (
                      <span key={item.id} className="mini-item-chip">
                        {item.quantity}× {item.foodName}
                      </span>
                    ))}
                  </div>

                  {/* Action Buttons */}
                  <div className="ticket-delivery-actions">
                    {isReady && (
                      <button
                        className="btn-action btn-pickup"
                        onClick={() => handlePickUp(order.id)}
                      >
                        <Navigation size={16} /> Accept & Pick Up Order
                      </button>
                    )}

                    {isOut && (
                      <button
                        className="btn-action btn-deliver"
                        onClick={() => handleDeliver(order.id)}
                      >
                        <CheckCircle2 size={16} /> Mark as Delivered
                      </button>
                    )}

                    {order.orderStatus === 'CONFIRMED' || order.orderStatus === 'PREPARING' ? (
                      <span className="kitchen-prep-notice">
                        <Clock size={14} /> Kitchen is preparing this order...
                      </span>
                    ) : null}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}