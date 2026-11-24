import { useState, useEffect } from 'react';
import { api } from '../services/api';
import InvoiceModal from '../components/ui/InvoiceModal';
import { FileText, ArrowRight, RefreshCw, ShoppingBag, Clock } from 'lucide-react';

export default function OrderHistory({ onTrackOrder }) {
  const [orders, setOrders] = useState([]);
  const [selectedInvoiceOrder, setSelectedInvoiceOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const data = await api.getMyOrders();
      setOrders(data);
    } catch {
      // Ignored
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="orders-page-container">
      <div className="orders-page-header">
        <div>
          <h2>Your Orders & Invoices</h2>
          <p>Track live deliveries and view GST-compliant tax invoices.</p>
        </div>
        <button className="btn-secondary" onClick={loadOrders}>
          <RefreshCw size={14} /> Refresh Orders
        </button>
      </div>

      {loading ? (
        <div className="orders-loading">
          <RefreshCw className="spinner" size={24} />
          <p>Loading your orders...</p>
        </div>
      ) : orders.length === 0 ? (
        <div className="portal-empty-card">
          <ShoppingBag size={40} className="empty-icon" />
          <h4>No orders placed yet</h4>
          <p>Browse our partner kitchens and place your first health-aware meal order!</p>
        </div>
      ) : (
        <div className="orders-list-grid">
          {orders.map((order) => {
            const formattedDate = new Date(order.createdAt).toLocaleDateString('en-IN', {
              day: 'numeric',
              month: 'short',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit',
            });

            return (
              <div key={order.id} className="order-history-card">
                <div className="order-card-header">
                  <div>
                    <span className="order-number-tag">{order.orderNumber}</span>
                    <h4 className="order-rest-name">{order.restaurantName}</h4>
                    <span className="order-date-text">{formattedDate}</span>
                  </div>
                  <div className="order-status-col">
                    <span className={`status-pill ${order.orderStatus.toLowerCase()}`}>
                      {order.orderStatus.replace(/_/g, ' ')}
                    </span>
                    <span className="order-grand-total">₹{order.grandTotal.toFixed(2)}</span>
                  </div>
                </div>

                <div className="order-card-items">
                  {order.items?.map((item) => (
                    <div key={item.id} className="order-item-snippet">
                      <span>{item.quantity}× {item.foodName}</span>
                      <span className="item-snippet-price">₹{item.subtotal}</span>
                    </div>
                  ))}
                </div>

                <div className="order-card-footer">
                  <span className="order-pay-info">
                    Paid via <strong>{order.paymentMethod}</strong> ({order.paymentStatus})
                  </span>
                  <div className="order-actions-row">
                    <button
                      className="btn-secondary btn-sm"
                      onClick={() => setSelectedInvoiceOrder(order)}
                    >
                      <FileText size={14} /> Tax Invoice
                    </button>
                    <button
                      className="btn-primary btn-sm"
                      onClick={() => onTrackOrder(order)}
                    >
                      Live Tracking <ArrowRight size={14} />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {selectedInvoiceOrder && (
        <InvoiceModal
          order={selectedInvoiceOrder}
          onClose={() => setSelectedInvoiceOrder(null)}
        />
      )}
    </div>
  );
}