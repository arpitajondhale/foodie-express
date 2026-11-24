import { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { HealthProfileProvider } from './context/HealthProfileContext';
import { CartProvider } from './context/CartContext';
import Header from './components/ui/Header';
import HealthAdvisoryModal from './components/ui/HealthAdvisoryModal';
import CustomerStorefront from './features/CustomerStorefront';
import CartDrawer from './features/CartDrawer';
import CheckoutModal from './features/CheckoutModal';
import LiveOrderTracker from './features/LiveOrderTracker';
import HealthProfileSettings from './features/HealthProfileSettings';
import RestaurantDashboard from './features/RestaurantDashboard';
import DeliveryDashboard from './features/DeliveryDashboard';
import AdminDashboard from './features/AdminDashboard';
import OrderHistory from './features/OrderHistory';
import AuthModal from './components/ui/AuthModal';

function MainApp() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('store'); // 'store' | 'orders' | 'tracking' | 'health' | 'restaurant' | 'delivery' | 'admin'
  const [activeTrackingOrder, setActiveTrackingOrder] = useState(null);
  const [cartOpen, setCartOpen] = useState(false);
  const [checkoutOpen, setCheckoutOpen] = useState(false);
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem('foodie_theme') === 'dark';
  });

  useEffect(() => {
    if (darkMode) {
      document.body.classList.add('dark-theme');
      localStorage.setItem('foodie_theme', 'dark');
    } else {
      document.body.classList.remove('dark-theme');
      localStorage.setItem('foodie_theme', 'light');
    }
  }, [darkMode]);

  const handleOrderPlaced = (placedOrder) => {
    setActiveTrackingOrder(placedOrder);
    setActiveTab('tracking');
  };

  const handleTrackExistingOrder = (order) => {
    setActiveTrackingOrder(order);
    setActiveTab('tracking');
  };

  return (
    <div className={`app-root ${darkMode ? 'dark' : 'light'}`}>
      {/* Universal Header */}
      <Header
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onOpenAuth={() => setAuthModalOpen(true)}
        onOpenCart={() => setCartOpen(true)}
        darkMode={darkMode}
        setDarkMode={setDarkMode}
      />

      {/* Main Body Routing */}
      <main className="main-content">
        {activeTab === 'store' && (
          <CustomerStorefront />
        )}

        {activeTab === 'orders' && (
          <OrderHistory onTrackOrder={handleTrackExistingOrder} />
        )}

        {activeTab === 'tracking' && (
          <LiveOrderTracker
            orderId={activeTrackingOrder?.id}
            initialOrder={activeTrackingOrder}
            onBackToStore={() => setActiveTab('store')}
          />
        )}

        {activeTab === 'health' && (
          <HealthProfileSettings />
        )}

        {activeTab === 'restaurant' && (
          <RestaurantDashboard />
        )}

        {activeTab === 'delivery' && (
          <DeliveryDashboard />
        )}

        {activeTab === 'admin' && (
          <AdminDashboard />
        )}
      </main>

      {/* Global Modals & Drawers */}
      <CartDrawer
        isOpen={cartOpen}
        onClose={() => setCartOpen(false)}
        onProceedToCheckout={() => {
          setCartOpen(false);
          setCheckoutOpen(true);
        }}
      />

      <CheckoutModal
        isOpen={checkoutOpen}
        onClose={() => setCheckoutOpen(false)}
        onOrderPlaced={handleOrderPlaced}
      />

      <HealthAdvisoryModal />

      <AuthModal
        isOpen={authModalOpen}
        onClose={() => setAuthModalOpen(false)}
      />

      {/* Footer */}
      <footer className="site-footer">
        <div className="footer-container">
          <div className="footer-col">
            <span className="footer-brand">🥗 Foodie Express</span>
            <p>India's first clinical health-aware food delivery platform. Clinical macro calculation, automated allergen guardrails, and real-time live GPS tracking.</p>
          </div>
          <div className="footer-col">
            <h4>Quick Demo Roles (Password@123)</h4>
            <ul>
              <li><code>customer@foodie.com</code> (Diabetic & Hypertension Profile)</li>
              <li><code>owner@foodie.com</code> (Kitchen & Stock Dashboard)</li>
              <li><code>rider@foodie.com</code> (Delivery Fleet App)</li>
              <li><code>admin@foodie.com</code> (Super Admin Console)</li>
            </ul>
          </div>
          <div className="footer-col">
            <h4>API Documentation</h4>
            <ul>
              <li><a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noreferrer">OpenAPI 3 / Swagger UI</a></li>
              <li><a href="http://localhost:8080/v3/api-docs" target="_blank" rel="noreferrer">Raw API Schema (JSON)</a></li>
              <li><a href="http://localhost:8080/h2-console" target="_blank" rel="noreferrer">H2 Console Database</a></li>
            </ul>
          </div>
        </div>
        <div className="footer-bottom">
          © 2026 Foodie Express Enterprise. All rights reserved. Built with Spring Boot 3.3.5 & React 18.
        </div>
      </footer>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <HealthProfileProvider>
        <CartProvider>
          <MainApp />
        </CartProvider>
      </HealthProfileProvider>
    </AuthProvider>
  );
}