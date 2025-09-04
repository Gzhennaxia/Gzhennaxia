import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import AdminLayout from './AdminLayout';
import DictList from './DictManagement/DictList';
import DictDetail from './DictManagement/DictDetail';

const AdminDashboard: React.FC = () => {
  return (
    <div>
      <h2>管理后台仪表盘</h2>
      <p>欢迎使用管理后台</p>
    </div>
  );
};

const AdminRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<AdminLayout />}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<AdminDashboard />} />
        <Route path="dict" element={<DictList />} />
        <Route path="dict/:code" element={<DictDetail />} />
      </Route>
    </Routes>
  );
};

export default AdminRoutes;