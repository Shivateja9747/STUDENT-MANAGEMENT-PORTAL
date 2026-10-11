import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (storedToken && storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  const login = (authData) => {
    localStorage.setItem('token', authData.token);
    const userData = {
      id: authData.id,
      username: authData.username,
      name: authData.name,
      role: authData.role,
      mustChangePassword: !!authData.mustChangePassword
    };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  };

  const updateMustChangePassword = (flag) => {
    if (user) {
      const updated = { ...user, mustChangePassword: flag };
      localStorage.setItem('user', JSON.stringify(updated));
      setUser(updated);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, updateMustChangePassword, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
