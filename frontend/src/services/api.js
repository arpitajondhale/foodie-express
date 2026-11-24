const API_BASE = 'http://localhost:8080/api';

class ApiService {
  getToken() {
    try {
      const auth = JSON.parse(localStorage.getItem('foodie_auth') || '{}');
      return auth.accessToken || null;
    } catch {
      return null;
    }
  }

  getRefreshToken() {
    try {
      const auth = JSON.parse(localStorage.getItem('foodie_auth') || '{}');
      return auth.refreshToken || null;
    } catch {
      return null;
    }
  }

  setTokens(accessToken, refreshToken, user) {
    localStorage.setItem('foodie_auth', JSON.stringify({ accessToken, refreshToken, user }));
  }

  clearAuth() {
    localStorage.removeItem('foodie_auth');
  }

  async request(endpoint, options = {}) {
    const token = this.getToken();
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers,
    };

    let response = await fetch(`${API_BASE}${endpoint}`, config);

    // If 401 Unauthorized and refresh token exists, attempt token rotation
    if (response.status === 401 && this.getRefreshToken()) {
      try {
        const refreshRes = await fetch(`${API_BASE}/auth/refresh`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken: this.getRefreshToken() }),
        });

        if (refreshRes.ok) {
          const authData = await refreshRes.json();
          this.setTokens(authData.accessToken, authData.refreshToken, authData.user);
          headers['Authorization'] = `Bearer ${authData.accessToken}`;
          response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
        } else {
          this.clearAuth();
        }
      } catch {
        this.clearAuth();
      }
    }

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      const error = new Error(data.detail || data.message || 'An error occurred while connecting to server.');
      error.status = response.status;
      error.fieldErrors = data.fieldErrors || null;
      throw error;
    }

    return data;
  }

  // --- Auth APIs ---
  signup(data) { return this.request('/auth/signup', { method: 'POST', body: JSON.stringify(data) }); }
  login(data) { return this.request('/auth/login', { method: 'POST', body: JSON.stringify(data) }); }
  getMe() { return this.request('/auth/me'); }

  // --- Restaurant APIs ---
  getRestaurants(params = {}) {
    const query = new URLSearchParams(params).toString();
    return this.request(`/restaurants${query ? `?${query}` : ''}`);
  }
  getRestaurantById(id) { return this.request(`/restaurants/${id}`); }
  getRestaurantMenu(id) { return this.request(`/restaurants/${id}/menu`); }
  toggleRestaurantOpen(id) { return this.request(`/restaurants/${id}/toggle-open`, { method: 'PATCH' }); }

  // --- Food Items ---
  getFoods(params = {}) {
    const query = new URLSearchParams(params).toString();
    return this.request(`/foods${query ? `?${query}` : ''}`);
  }
  getFoodById(id) { return this.request(`/foods/${id}`); }
  updateFoodAvailability(id, available) {
    return this.request(`/foods/${id}/availability?available=${available}`, { method: 'PATCH' });
  }

  // --- Health Guardrail APIs ---
  evaluateCartItem(foodItemId, userId) {
    const query = userId ? `?foodItemId=${foodItemId}&userId=${userId}` : `?foodItemId=${foodItemId}`;
    return this.request(`/health-guardrail/evaluate${query}`);
  }
  getDietaryTags(foodId) { return this.request(`/health-guardrail/dietary-tags/${foodId}`); }

  // --- Health Profile APIs ---
  getHealthProfile() { return this.request('/health-profile'); }
  updateHealthProfile(data) { return this.request('/health-profile', { method: 'PUT', body: JSON.stringify(data) }); }

  // --- Address APIs ---
  getAddresses() { return this.request('/addresses'); }
  addAddress(data) { return this.request('/addresses', { method: 'POST', body: JSON.stringify(data) }); }
  deleteAddress(id) { return this.request(`/addresses/${id}`, { method: 'DELETE' }); }

  // --- Coupons ---
  getCoupons() { return this.request('/coupons'); }
  validateCoupon(code, subtotal) {
    return this.request('/coupons/validate', { method: 'POST', body: JSON.stringify({ code, subtotal }) });
  }

  // --- Orders ---
  createOrder(data) { return this.request('/orders', { method: 'POST', body: JSON.stringify(data) }); }
  getMyOrders() { return this.request('/orders'); }
  getOrderById(id) { return this.request(`/orders/${id}`); }
  getOrderByNumber(number) { return this.request(`/orders/number/${number}`); }
  getRestaurantOrders(restaurantId) { return this.request(`/orders/restaurant/${restaurantId}`); }
  getDeliveryQueue() { return this.request('/orders/delivery-queue'); }
  updateOrderStatus(id, status, cancellationReason = null) {
    return this.request(`/orders/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ orderStatus: status, cancellationReason }),
    });
  }
  assignRider(id) { return this.request(`/orders/${id}/assign-rider`, { method: 'PATCH' }); }

  // --- Reviews ---
  getRestaurantReviews(restaurantId) { return this.request(`/reviews/restaurant/${restaurantId}`); }
  addReview(data) { return this.request('/reviews', { method: 'POST', body: JSON.stringify(data) }); }

  // --- Admin ---
  getAdminDashboard() { return this.request('/admin/dashboard'); }
  getAdminUsers() { return this.request('/admin/users'); }
  toggleUserActive(id) { return this.request(`/admin/users/${id}/toggle-active`, { method: 'PATCH' }); }
}

export const api = new ApiService();