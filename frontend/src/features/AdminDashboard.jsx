import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import {
  ShieldAlert,
  Users,
  UtensilsCrossed,
  Tag,
  TrendingUp,
  ShoppingBag,
  ToggleLeft,
  ToggleRight,
  RefreshCw,
  CheckCircle,
} from 'lucide-react';

export default function AdminDashboard() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [users, setUsers] = useState([]);
  const [restaurants, setRestaurants] = useState([]);
  const [coupons, setCoupons] = useState([]);
  const [activeTab, setActiveTab] = useState('overview'); // 'overview' | 'users' | 'restaurants'
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAll();
  }, []);

  const loadAll = async () => {
    setLoading(true);
    try {
      const [dash, uList, rList, cList] = await Promise.all([
        api.getAdminDashboard(),
        api.getAdminUsers(),
        api.getRestaurants(),
        api.getCoupons(),
      ]);
      setDashboard(dash);
      setUsers(uList);
      setRestaurants(rList);
      setCoupons(cList);
    } catch {
      // Ignored
    } finally {
      setLoading(false);
    }
  };

  const handleToggleUser = async (userId) => {
    try {
      await api.toggleUserActive(userId);
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, active: !u.active } : u))
      );
    } catch (err) {
      alert(err.message || 'Failed to toggle user status');
    }
  };

  const handleToggleRestaurant = async (restId) => {
    try {
      await api.toggleRestaurantOpen(restId);
      setRestaurants((prev) =>
        prev.map((r) => (r.id === restId ? { ...r, open: !r.open } : r))
      );
    } catch (err) {
      alert(err.message || 'Failed to toggle restaurant');
    }
  };

  return (
    <div className="portal-page">
      <div className="portal-header">
        <div className="portal-title-group">
          <div className="portal-icon admin-icon">
            <ShieldAlert size={28} />
          </div>
          <div>
            <h2>Super Admin Platform Console</h2>
            <p>Logged in as: <strong>{user?.name || 'Priya Nair'}</strong> (Platform SuperAdmin)</p>
          </div>
        </div>

        <div className="portal-nav-tabs">
          <button
            className={`portal-tab ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            KPI Overview
          </button>
          <button
            className={`portal-tab ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            User RBAC Accounts ({users.length})
          </button>
          <button
            className={`portal-tab ${activeTab === 'restaurants' ? 'active' : ''}`}
            onClick={() => setActiveTab('restaurants')}
          >
            Partner Kitchens ({restaurants.length})
          </button>
        </div>
      </div>

      <div className="portal-content">
        {/* Tab 1: KPI Overview */}
        {activeTab === 'overview' && (
          <div>
            <div className="analytics-kpi-grid admin-kpi-grid">
              <div className="kpi-card">
                <span className="kpi-label">Total Platform GMV</span>
                <span className="kpi-val">₹{dashboard?.totalGmv?.toFixed(2) || '0.00'}</span>
              </div>
              <div className="kpi-card">
                <span className="kpi-label">Active Orders</span>
                <span className="kpi-val">{dashboard?.activeOrders || 0}</span>
              </div>
              <div className="kpi-card">
                <span className="kpi-label">Total Lifetime Orders</span>
                <span className="kpi-val">{dashboard?.totalOrders || 0}</span>
              </div>
              <div className="kpi-card">
                <span className="kpi-label">Registered Users</span>
                <span className="kpi-val">{dashboard?.totalUsers || 0}</span>
              </div>
              <div className="kpi-card">
                <span className="kpi-label">Active Restaurants</span>
                <span className="kpi-val">{dashboard?.activeRestaurants || 0}</span>
              </div>
              <div className="kpi-card">
                <span className="kpi-label">Active Promo Coupons</span>
                <span className="kpi-val">{dashboard?.activeCoupons || 0}</span>
              </div>
            </div>

            {/* Coupons Card */}
            <div className="portal-sub-card">
              <h3>Active Promotional Coupons</h3>
              <div className="coupons-table-wrap">
                <table className="portal-table">
                  <thead>
                    <tr>
                      <th>Coupon Code</th>
                      <th>Type</th>
                      <th>Discount</th>
                      <th>Min Order</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {coupons.map((c) => (
                      <tr key={c.id}>
                        <td><strong>{c.code}</strong></td>
                        <td>{c.discountType}</td>
                        <td>{c.discountType === 'PERCENTAGE' ? `${c.discountValue}%` : `₹${c.discountValue}`}</td>
                        <td>₹{c.minOrderAmount}</td>
                        <td><span className="badge badge-success">Active</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Users RBAC */}
        {activeTab === 'users' && (
          <div className="portal-sub-card">
            <div className="queue-header-row">
              <h3>Registered Users & RBAC Roles</h3>
              <button className="btn-secondary" onClick={loadAll}>
                <RefreshCw size={14} /> Refresh
              </button>
            </div>

            <table className="portal-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Account Status</th>
                  <th className="text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>#{u.id}</td>
                    <td><strong>{u.name}</strong></td>
                    <td>{u.email}</td>
                    <td>
                      <span className={`badge badge-${u.role?.toLowerCase().replace('role_', '')}`}>
                        {u.role}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${u.active !== false ? 'badge-success' : 'badge-danger'}`}>
                        {u.active !== false ? 'Active' : 'Suspended'}
                      </span>
                    </td>
                    <td className="text-right">
                      <button
                        className="btn-secondary btn-sm"
                        onClick={() => handleToggleUser(u.id)}
                      >
                        {u.active !== false ? 'Suspend' : 'Activate'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Tab 3: Partner Kitchens */}
        {activeTab === 'restaurants' && (
          <div className="portal-sub-card">
            <div className="queue-header-row">
              <h3>Platform Partner Restaurants</h3>
              <button className="btn-secondary" onClick={loadAll}>
                <RefreshCw size={14} /> Refresh
              </button>
            </div>

            <table className="portal-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Restaurant Name</th>
                  <th>Cuisines</th>
                  <th>Address</th>
                  <th>Rating</th>
                  <th>Kitchen Status</th>
                  <th className="text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {restaurants.map((r) => (
                  <tr key={r.id}>
                    <td>#{r.id}</td>
                    <td><strong>{r.name}</strong></td>
                    <td>{Array.from(r.cuisineTypes || []).join(', ')}</td>
                    <td>{r.address}</td>
                    <td>★ {r.rating} ({r.totalRatings})</td>
                    <td>
                      <span className={`badge ${r.open ? 'badge-success' : 'badge-warning'}`}>
                        {r.open ? 'Accepting Orders' : 'Closed'}
                      </span>
                    </td>
                    <td className="text-right">
                      <button
                        className="btn-secondary btn-sm"
                        onClick={() => handleToggleRestaurant(r.id)}
                      >
                        {r.open ? 'Mark Closed' : 'Mark Open'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}