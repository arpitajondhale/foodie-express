import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { X, Lock, Mail, User, Phone, CheckCircle2 } from 'lucide-react';

export default function AuthModal({ isOpen, onClose }) {
  const { login, signup, demoLogin, loading } = useAuth();
  const [isSignup, setIsSignup] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('ROLE_CUSTOMER');
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (isSignup) {
        await signup({ name, email, phone, password, role });
      } else {
        await login(email, password);
      }
      onClose();
    } catch (err) {
      setError(err.message || 'Authentication failed. Please verify credentials.');
    }
  };

  const handleDemoClick = async (roleKey) => {
    setError('');
    try {
      await demoLogin(roleKey);
      onClose();
    } catch (err) {
      setError(err.message || 'Demo login failed');
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal-container auth-modal">
        <div className="modal-header">
          <div>
            <h3>{isSignup ? 'Create Foodie Express Account' : 'Welcome Back'}</h3>
            <span className="modal-subtitle">
              {isSignup ? 'Set up your health profile and start ordering' : 'Sign in to access your orders and health settings'}
            </span>
          </div>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {/* Demo Fast Login Pills */}
        <div className="demo-accounts-box">
          <span className="demo-title">1-Click Demo Accounts (Password@123):</span>
          <div className="demo-pills-row">
            <button type="button" className="demo-pill" onClick={() => handleDemoClick('customer')}>
              Aarav (Customer)
            </button>
            <button type="button" className="demo-pill" onClick={() => handleDemoClick('owner')}>
              Vikram (Kitchen)
            </button>
            <button type="button" className="demo-pill" onClick={() => handleDemoClick('rider')}>
              Rajesh (Rider)
            </button>
            <button type="button" className="demo-pill" onClick={() => handleDemoClick('admin')}>
              Priya (Admin)
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-alert">{error}</div>}

          {isSignup && (
            <>
              <div className="input-group">
                <label>Full Name</label>
                <div className="input-icon-wrap">
                  <User size={16} />
                  <input
                    type="text"
                    required
                    placeholder="Enter your name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                  />
                </div>
              </div>

              <div className="input-group">
                <label>Mobile Number</label>
                <div className="input-icon-wrap">
                  <Phone size={16} />
                  <input
                    type="tel"
                    placeholder="10-digit mobile number"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  />
                </div>
              </div>

              <div className="input-group">
                <label>Account Role</label>
                <select value={role} onChange={(e) => setRole(e.target.value)}>
                  <option value="ROLE_CUSTOMER">Customer (Order Meals)</option>
                  <option value="ROLE_RESTAURANT_OWNER">Restaurant Owner (Kitchen)</option>
                  <option value="ROLE_DELIVERY_PARTNER">Delivery Partner (Rider)</option>
                  <option value="ROLE_ADMIN">Platform Super Admin</option>
                </select>
              </div>
            </>
          )}

          <div className="input-group">
            <label>Email Address</label>
            <div className="input-icon-wrap">
              <Mail size={16} />
              <input
                type="email"
                required
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          <div className="input-group">
            <label>Password</label>
            <div className="input-icon-wrap">
              <Lock size={16} />
              <input
                type="password"
                required
                placeholder="At least 6 characters"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <button type="submit" className="btn-primary auth-submit-btn" disabled={loading}>
            {loading ? 'Please wait...' : isSignup ? 'Sign Up' : 'Sign In'}
          </button>
        </form>

        <div className="auth-switch-prompt">
          {isSignup ? (
            <p>
              Already have an account?{' '}
              <button type="button" className="text-link" onClick={() => setIsSignup(false)}>
                Sign In
              </button>
            </p>
          ) : (
            <p>
              Don't have an account yet?{' '}
              <button type="button" className="text-link" onClick={() => setIsSignup(true)}>
                Sign Up
              </button>
            </p>
          )}
        </div>
      </div>
    </div>
  );
}