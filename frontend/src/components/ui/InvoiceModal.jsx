import { X, Printer, CheckCircle2 } from 'lucide-react';

export default function InvoiceModal({ order, onClose }) {
  if (!order) return null;

  const handlePrint = () => {
    window.print();
  };

  const formattedDate = new Date(order.createdAt || Date.now()).toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });

  return (
    <div className="modal-backdrop">
      <div className="modal-container invoice-modal">
        <div className="invoice-actions-top no-print">
          <button className="btn-secondary" onClick={handlePrint}>
            <Printer size={16} /> Print / Save PDF
          </button>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="invoice-printable" id="printable-invoice">
          {/* Header */}
          <div className="invoice-header">
            <div className="invoice-brand">
              <h2>Foodie Express</h2>
              <span className="invoice-tagline">Health-Aware Online Food Delivery</span>
              <span className="invoice-gstin">GSTIN: 29AABCF1234F1Z5</span>
            </div>
            <div className="invoice-meta">
              <span className="invoice-badge">TAX INVOICE</span>
              <div className="meta-line">
                <strong>Invoice / Order:</strong> {order.orderNumber}
              </div>
              <div className="meta-line">
                <strong>Date & Time:</strong> {formattedDate}
              </div>
              <div className="meta-line">
                <strong>Status:</strong> {order.orderStatus}
              </div>
            </div>
          </div>

          <div className="invoice-divider" />

          {/* Restaurant & Customer Info */}
          <div className="invoice-parties-grid">
            <div className="party-box">
              <h4>Billed From (Restaurant):</h4>
              <p className="party-name">{order.restaurantName || 'Foodie Express Kitchen'}</p>
              <p className="party-sub">FSSAI Lic. No: 11223344556677</p>
              <p className="party-sub">Bengaluru, Karnataka - 560034</p>
            </div>
            <div className="party-box">
              <h4>Billed & Delivered To:</h4>
              <p className="party-name">{order.customerName}</p>
              <p className="party-sub">Phone: {order.customerPhone || 'N/A'}</p>
              <p className="party-sub">{order.deliveryAddressSnapshot}</p>
            </div>
          </div>

          {/* Items Table */}
          <table className="invoice-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Item Description & Customizations</th>
                <th className="text-right">Price</th>
                <th className="text-center">Qty</th>
                <th className="text-right">Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {order.items?.map((item, idx) => (
                <tr key={idx}>
                  <td>{idx + 1}</td>
                  <td>
                    <div className="item-title">{item.foodName}</div>
                    {item.customizationDetails && (
                      <div className="item-cust">({item.customizationDetails})</div>
                    )}
                  </td>
                  <td className="text-right">₹{item.price}</td>
                  <td className="text-center">{item.quantity}</td>
                  <td className="text-right">₹{item.subtotal}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Total Calculation Breakdown */}
          <div className="invoice-summary-grid">
            <div className="payment-info-box">
              <h4>Payment Summary</h4>
              <div className="pay-line">
                <CheckCircle2 size={16} className="text-success" />
                <span>
                  Method: <strong>{order.paymentMethod}</strong> ({order.paymentStatus})
                </span>
              </div>
              <p className="tax-note">
                * This is a computer-generated tax invoice and does not require a physical signature.
              </p>
            </div>

            <div className="breakdown-totals-box">
              <div className="breakdown-line">
                <span>Items Subtotal:</span>
                <span>₹{order.itemTotal?.toFixed(2)}</span>
              </div>
              <div className="breakdown-line">
                <span>Packaging Fee:</span>
                <span>₹{order.packagingFee?.toFixed(2)}</span>
              </div>
              <div className="breakdown-line">
                <span>Delivery Charges:</span>
                <span>{order.deliveryFee === 0 ? 'FREE' : `₹${order.deliveryFee?.toFixed(2)}`}</span>
              </div>
              <div className="breakdown-line">
                <span>GST (5%):</span>
                <span>₹{order.gstAmount?.toFixed(2)}</span>
              </div>
              {order.discountAmount > 0 && (
                <div className="breakdown-line discount-line">
                  <span>Coupon Discount:</span>
                  <span>-₹{order.discountAmount?.toFixed(2)}</span>
                </div>
              )}
              <div className="breakdown-total-line">
                <span>Grand Total:</span>
                <span>₹{order.grandTotal?.toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}