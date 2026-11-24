import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { useHealthProfile } from '../../context/HealthProfileContext';
import {
  ShieldCheck,
  ShoppingBag,
  User,
  HeartPulse,
  Sun,
  Moon,
  MapPin,
  LogIn,
  LogOut,
  ChefHat,
  Bike,
  ShieldAlert,
  Sliders,
} from 'lucide-react';

export default function Header({
  activeTab,
  setActiveTab,
  onOpenAuth,
  onOpenCart,
  darkMode,
  setDarkMode,
}) {
  const { user, demoLogin, logout } = useAuth();
  const { totalQuantity } = useCart();
  const { profile, safeOnly, setSafeOnly } = useHealthProfile();
  const [roleMenuOpen, setRoleMenuOpen] = useState(false);

  const getRoleBadge = () => {
    if (!user) return null;
    switch (user.role) {
      case 'ROLE_ADMIN':
        return <span className="badge badge-admin">Super Admin</span>;
      case 'ROLE_RESTAURANT_OWNER':
        return <span className="badge badge-owner">Kitchen Owner</span>;
      case 'ROLE_DELIVERY_PARTNER':
        return <span className="badge badge-rider">Delivery Partner</span>;
      default:
        return <span className="badge badge-customer">Customer</span>;
    }
  };

  const handleDemoSwitch = async (role) => {
    setRoleMenuOpen(false);
    await demoLogin(role);
    if (role === 'owner') setActiveTab('restaurant');
    else if (role === 'rider') setActiveTab('delivery');
    else if (role === 'admin') setActiveTab('admin');
    else setActiveTab('store');
  };

  return (
    <header className="site-header">
      <div className="header-container">
        {/* Logo & Location */}
        <div className="brand-group">
          <div className="brand-logo" onClick={() => setActiveTab('store')}>
            <span className="brand-icon">🥗</span>
            <div className="brand-text">
              <span className="brand-title">Foodie Express</span>
              <span className="brand-subtitle">Health-Aware Delivery</span>
            </div>
          </div>
          <div className="location-pill">
            <MapPin size={14} className="location-icon" />
            <span className="location-name">Bengaluru, Indiranagar</span>
          </div>
        </div>

        {/* Center: Health Guardrail Toggle */}
        <div className="header-health-toggle">
          <button
            type="button"
            className={`health-guard-pill ${safeOnly ? 'active' : ''}`}
            onClick={() => setSafeOnly(!safeOnly)}
            title="Filter menu items to only show dishes clinically safe for your diagnosed medical profile"
          >
            <ShieldCheck size={18} className="shield-icon" />
            <div className="guard-text">
              <span className="guard-title">Safe For My Health</span>
              <span className="guard-desc">
                {safeOnly
                  ? 'Active Filtering'
                  : profile?.conditions?.length
                  ? `${profile.conditions.length} condition(s) set`
                  : 'Click to filter'}
              </span>
            </div>
            <div className={`switch-knob ${safeOnly ? 'checked' : ''}`} />
          </button>
        </div>

        {/* Right: Actions */}
        <div className="header-actions">
          {/* Navigation links based on role */}
          {user?.role === 'ROLE_RESTAURANT_OWNER' && (
            <button
              className={`nav-btn ${activeTab === 'restaurant' ? 'active' : ''}`}
              onClick={() => setActiveTab('restaurant')}
            >
              <ChefHat size={16} /> Kitchen Queue
            </button>
          )}

          {user?.role === 'ROLE_DELIVERY_PARTNER' && (
            <button
              className={`nav-btn ${activeTab === 'delivery' ? 'active' : ''}`}
              onClick={() => setActiveTab('delivery')}
            >
              <Bike size={16} /> Deliveries
            </button>
          )}

          {user?.role === 'ROLE_ADMIN' && (
            <button
              className={`nav-btn ${activeTab === 'admin' ? 'active' : ''}`}
              onClick={() => setActiveTab('admin')}
            >
              <ShieldAlert size={16} /> Admin Console
            </button>
          )}

          {/* Customer links */}
          <button
            className={`nav-btn ${activeTab === 'store' ? 'active' : ''}`}
            onClick={() => setActiveTab('store')}
          >
            Storefront
          </button>

          {user && (
            <button
              className={`nav-btn ${activeTab === 'orders' ? 'active' : ''}`}
              onClick={() => setActiveTab('orders')}
            >
              Orders
            </button>
          )}

          {user?.role === 'ROLE_CUSTOMER' && (
            <button
              className={`nav-btn ${activeTab === 'health' ? 'active' : ''}`}
              onClick={() => setActiveTab('health')}
              title="Medical conditions & dietary preferences"
            >
              <HeartPulse size={16} /> Health Profile
            </button>
          )}

          {/* Cart Button */}
          <button className="cart-btn" onClick={onOpenCart}>
            <ShoppingBag size={18} />
            <span className="cart-label">Cart</span>
            {totalQuantity > 0 && <span className="cart-counter">{totalQuantity}</span>}
          </button>

          {/* Quick Demo Role Switcher */}
          <div className="role-switcher-dropdown">
            <button
              className="role-toggle-btn"
              onClick={() => setRoleMenuOpen(!roleMenuOpen)}
              title="Switch demo account roles"
            >
              {getRoleBadge() || <span className="badge badge-guest">Demo Roles</span>}
            </button>

            {roleMenuOpen && (
              <div className="dropdown-menu">
                <div className="dropdown-header">Switch Demo Role (Password@123)</div>
                <button onClick={() => handleDemoSwitch('customer')}>
                  <User size={14} /> Customer (Aarav - Diabetic Profile)
                </button>
                <button onClick={() => handleDemoSwitch('owner')}>
                  <ChefHat size={14} /> Restaurant Owner (Chef Vikram)
                </button>
                <button onClick={() => handleDemoSwitch('rider')}>
                  <Bike size={14} /> Delivery Partner (Rajesh)
                </button>
                <button onClick={() => handleDemoSwitch('admin')}>
                  <ShieldAlert size={14} /> Super Admin (Priya)
                </button>
              </div>
            )}
          </div>

          {/* Theme Toggle */}
          <button
            className="theme-btn"
            onClick={() => setDarkMode(!darkMode)}
            title="Toggle light/dark theme"
          >
            {darkMode ? <Sun size={17} /> : <Moon size={17} />}
          </button>

          {/* Auth Button */}
          {user ? (
            <div className="user-profile-menu">
              <span className="user-greeting">Hi, {user.name?.split(' ')[0]}</span>
              <button className="logout-btn" onClick={logout} title="Sign Out">
                <LogOut size={16} />
              </button>
            </div>
          ) : (
            <button className="login-btn" onClick={onOpenAuth}>
              <LogIn size={16} /> Sign In
            </button>
          )}
        </div>
      </div>
    </header>
  );
}