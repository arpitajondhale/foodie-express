import { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from './AuthContext';

const HealthProfileContext = createContext();

export function HealthProfileProvider({ children }) {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [safeOnly, setSafeOnly] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user && user.role === 'ROLE_CUSTOMER') {
      loadProfile();
    } else {
      setProfile(null);
      setSafeOnly(false);
    }
  }, [user]);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const data = await api.getHealthProfile();
      setProfile(data);
      if (data?.conditions?.length > 0 || data?.allergies?.length > 0) {
        setSafeOnly(true);
      }
    } catch {
      // Ignored for unauthenticated users
    } finally {
      setLoading(false);
    }
  };

  const updateProfile = async (newData) => {
    try {
      setLoading(true);
      const updated = await api.updateHealthProfile(newData);
      setProfile(updated);
      return updated;
    } finally {
      setLoading(false);
    }
  };

  return (
    <HealthProfileContext.Provider value={{ profile, safeOnly, setSafeOnly, loading, loadProfile, updateProfile }}>
      {children}
    </HealthProfileContext.Provider>
  );
}

export const useHealthProfile = () => useContext(HealthProfileContext);