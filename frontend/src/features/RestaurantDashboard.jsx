import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import {
  ChefHat,
  Clock,
  CheckCircle,
  PackageCheck,
  XCircle,
  TrendingUp,
  AlertCircle,
  ShoppingBag,
  ToggleLeft,
  ToggleRight,
  RefreshCw,
} from 'lucide-react';

export default function RestaurantDashboard() {
  const { user } = useAuth();
  const [restaurantId, setRestaurantId] = useState(1); // Default to Aura Pure Veg
  const [orders, setOrders] = useState([]);
  const [menuItems, setMenuItems] = useState([]);
  const [activeTab, setActiveTab] = useState('queue'); // 'queue' | 'menu' | 'analytics'
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
    const timer = setInterval(loadOrders, 5000);
    return () => clearInterval(timer);
  }, [restaurantId]);

  const loadData = async () => {
    setLoading(true);
    await Promise.all([loadOrders(), loadMenu()]);
    setLoading(false);
  };

  const loadOrders = async () => {
    try {
      const data = await api.getRestaurantOrders(restaurantId);
      setOrders(data);
    } catch {
      // Ignored
    }
  };

  const loadMenu = async () => {
    try {
      const foods = await api.getFoods({ restaurantId });
      setMenuItems(foods);
    } catch {
      // Ignored
    }
  };

  const handleStatusUpdate = async (orderId, newStatus) => {
    try {
      await api.updateOrderStatus(orderId, newStatus);
      await loadOrders();
    } catch (err) {
      alert(err.message || 'Status update failed');
    }
  };

  const handleToggleStock = async (dishId, currentStock) => {
    try {
      await api.updateFoodAvailability(dishId, !currentStock);
      setMenuItems((prev) =>
        prev.map((d) => (d.id === dishId ? { ...d, available: !currentStock } : d))
      );
    } catch (err) {
      alert(err.message || 'Failed to update stock');
    }
  };

  // Analytics metrics
  const totalRevenue = orders.reduce((sum, o) => sum + (o.grandTotal || 0), 0);
  const activeOrders = orders.filter((o) => o.orderStatus !== 'DELIVERED' && o.orderStatus !== 'CANCELLED');

  return (
    <div className="portal-page">
      <div className="portal-header">
        <div className="portal-title-group">
          <div className="portal-icon kitchen-icon">
            <ChefHat size={28} />
          </div>
          <div>
            <h2>Kitchen Operations & Menu Manager</h2>
            <p>Logged in as: <strong>{user?.name || 'Chef Vikram Oberoi'}</strong> (Aura Pure Veg & Diabetic Delights)</p>
          </div>
        </div>

        {/* View Switcher */}
        <div className="portal-nav-tabs">
          <button
            className={`portal-tab ${activeTab === 'queue' ? 'active' : ''}`}
            onClick={() => setActiveTab('queue')}
          >
            Live Kitchen Queue ({activeOrders.length})
          </button>
          <button
            className={`portal-tab ${activeTab === 'menu' ? 'active' : ''}`}
            onClick={() => setActiveTab('menu')}
          >
            Dish Stock & Availability ({menuItems.length})
          </button>
          <button
            className={`portal-tab ${activeTab === 'analytics' ? 'active' : ''}`}
            onClick={() => setActiveTab('analytics')}
          >
            Kitchen Analytics
          </button>
        </div>
      </div>

      {/* Tab 1: Live Kitchen Queue */}
      {activeTab === 'queue' && (
        <div className="portal-content">
          <div className="queue-header-row">
            <h3>Incoming Kitchen Tickets</h3>
            <button className="btn-secondary" onClick={loadOrders}>
              <RefreshCw size={14} /> Refresh Queue
            </button>
          </div>

          {orders.length === 0 ? (
            <div className="portal-empty-card">
              <ShoppingBag size={40} className="empty-icon" />
              <h4>No orders in kitchen queue</h4>
              <p>Place an order from the storefront as a customer to see it appear here in real-time!</p>
            </div>
          ) : (
            <div className="kitchen-orders-grid">
              {orders.map((order) => {
                const isPending = order.orderStatus === 'PLACED';
                const isConfirmed = order.orderStatus === 'CONFIRMED';
                const isPreparing = order.orderStatus === 'PREPARING';
                const isReady = order.orderStatus === 'READY_FOR_PICKUP';
                const isOut = order.orderStatus === 'OUT_FOR_DELIVERY';
                const isDelivered = order.orderStatus === 'DELIVERED';

                return (
                  <div key={order.id} className={`kitchen-order-ticket status-${order.orderStatus.toLowerCase()}`}>
                    <div className="ticket-header">
                      <div>
                        <span className="ticket-number">{order.orderNumber}</span>
                        <span className="ticket-time">
                          {new Date(order.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>
                      <span className={`status-tag ${order.orderStatus.toLowerCase()}`}>
                        {order.orderStatus.replace(/_/g, ' ')}
                      </span>
                    </div>

                    <div className="ticket-customer">
                      <strong>Customer:</strong> {order.customerName} ({order.customerPhone || 'N/A'})
                    </div>

                    {/* Items List */}
                    <div className="ticket-items">
                      {order.items?.map((item) => (
                        <div key={item.id} className="ticket-item-row">
                          <span className="ticket-qty">{item.quantity}×</span>
                          <span className="ticket-name">{item.foodName}</span>
                          {item.customizationDetails && (
                            <span className="ticket-cust">({item.customizationDetails})</span>
                          )}
                        </div>
                      ))}
                    </div>

                    <div className="ticket-address">
                      <Clock size={13} /> {order.deliveryAddressSnapshot}
                    </div>

                    {/* Action Buttons for Kitchen Lifecycle */}
                    <div className="ticket-actions">
                      {isPending && (
                        <>
                          <button
                            className="btn-action btn-confirm"
                            onClick={() => handleStatusUpdate(order.id, 'CONFIRMED')}
                          >
                            <CheckCircle size={15} /> Accept Order
                          </button>
                          <button
                            className="btn-action btn-reject"
                            onClick={() => handleStatusUpdate(order.id, 'CANCELLED')}
                          >
                            <XCircle size={15} /> Reject
                          </button>
                        </>
                      )}

                      {isConfirmed && (
                        <button
                          className="btn-action btn-preparing"
                          onClick={() => handleStatusUpdate(order.id, 'PREPARING')}
                        >
                          <ChefHat size={15} /> Start Preparing
                        </button>
                      )}

                      {isPreparing && (
                        <button
                          className="btn-action btn-ready"
                          onClick={() => handleStatusUpdate(order.id, 'READY_FOR_PICKUP')}
                        >
                          <PackageCheck size={15} /> Ready for Pickup
                        </button>
                      )}

                      {isReady && (
                        <div className="waiting-pickup-note">
                          Awaiting Delivery Partner Pickup
                        </div>
                      )}

                      {isOut && (
                        <div className="in-transit-note">
                          Out for Delivery with Rider
                        </div>
                      )}

                      {isDelivered && (
                        <div className="delivered-note">
                          ✓ Completed & Delivered
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Menu & Stock Manager */}
      {activeTab === 'menu' && (
        <div className="portal-content">
          <div className="queue-header-row">
            <h3>Dishes & Stock Availability Toggle</h3>
            <span className="section-sub">Toggle dishes 'Sold Out' during rush hours</span>
          </div>

          <div className="menu-stock-table-card">
            <table className="portal-table">
              <thead>
                <tr>
                  <th>Dish</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Calories / Protein</th>
                  <th>Stock Status</th>
                  <th className="text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {menuItems.map((dish) => (
                  <tr key={dish.id}>
                    <td>
                      <div className="table-dish-title">
                        <span className={`diet-dot-sm ${dish.dietType?.toLowerCase()}`} />
                        <strong>{dish.name}</strong>
                      </div>
                    </td>
                    <td>{dish.categoryName}</td>
                    <td>₹{dish.price}</td>
                    <td>
                      {dish.nutritionalInfo
                        ? `${dish.nutritionalInfo.calories} kcal • ${dish.nutritionalInfo.proteinGrams}g`
                        : 'N/A'}
                    </td>
                    <td>
                      <span className={`stock-badge ${dish.available ? 'in-stock' : 'sold-out'}`}>
                        {dish.available ? 'In Stock' : 'Sold Out'}
                      </span>
                    </td>
                    <td className="text-right">
                      <button
                        className={`toggle-stock-btn ${dish.available ? 'active' : ''}`}
                        onClick={() => handleToggleStock(dish.id, dish.available)}
                      >
                        {dish.available ? <ToggleRight size={22} className="text-success" /> : <ToggleLeft size={22} className="text-muted" />}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 3: Analytics */}
      {activeTab === 'analytics' && (
        <div className="portal-content">
          <div className="analytics-kpi-grid">
            <div className="kpi-card">
              <span className="kpi-label">Today's Revenue</span>
              <span className="kpi-val">₹{totalRevenue.toFixed(2)}</span>
            </div>
            <div className="kpi-card">
              <span className="kpi-label">Total Kitchen Orders</span>
              <span className="kpi-val">{orders.length}</span>
            </div>
            <div className="kpi-card">
              <span className="kpi-label">Active Preparing</span>
              <span className="kpi-val">{activeOrders.length}</span>
            </div>
            <div className="kpi-card">
              <span className="kpi-label">Average Food Rating</span>
              <span className="kpi-val">★ 4.8 / 5.0</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}