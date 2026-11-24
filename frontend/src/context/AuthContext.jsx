import { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const auth = JSON.parse(localStorage.getItem('foodie_auth') || '{}');
      return auth.user || null;
    } catch {
      return null;
    }
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (api.getToken() && !user) {
      api.getMe()
        .then((userData) => setUser(userData))
        .catch(() => api.clearAuth());
    }
  }, []);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const res = await api.login({ email, password });
      api.setTokens(res.accessToken, res.refreshToken, res.user);
      setUser(res.user);
      return res.user;
    } finally {
      setLoading(false);
    }
  };

  const demoLogin = async (roleKey) => {
    const creds = {
      customer: { email: 'customer@foodie.com', password: 'Password@123' },
      owner: { email: 'owner@foodie.com', password: 'Password@123' },
      rider: { email: 'rider@foodie.com', password: 'Password@123' },
      admin: { email: 'admin@foodie.com', password: 'Password@123' },
    };
    const c = creds[roleKey] || creds.customer;
    return login(c.email, c.password);
  };

  const signup = async (data) => {
    setLoading(true);
    try {
      const res = await api.signup(data);
      api.setTokens(res.accessToken, res.refreshToken, res.user);
      setUser(res.user);
      return res.user;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    api.clearAuth();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, demoLogin, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);