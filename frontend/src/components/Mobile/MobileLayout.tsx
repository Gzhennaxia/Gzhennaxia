import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  HomeOutlined, 
  CalendarOutlined, 
  UserOutlined,
  PlusOutlined,
  MenuOutlined,
  BellOutlined
} from '@ant-design/icons';
import { FloatButton, Badge } from 'antd';
import TaskFormModal from '../TaskForm/TaskFormModal';
import './MobileLayout.css';

interface MobileLayoutProps {
  children: React.ReactNode;
}

const MobileLayout: React.FC<MobileLayoutProps> = ({ children }) => {
  const [taskModalVisible, setTaskModalVisible] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleAddTask = () => {
    setTaskModalVisible(true);
  };

  const handleTaskSuccess = () => {
    setTaskModalVisible(false);
    // 刷新当前页面数据
    window.location.reload();
  };

  const navItems = [
    { key: '/', icon: <HomeOutlined />, label: '任务', path: '/' },
    { key: '/calendar', icon: <CalendarOutlined />, label: '日历', path: '/calendar' },
    { key: '/profile', icon: <UserOutlined />, label: '我的', path: '/profile' },
  ];

  return (
    <div className="mobile-layout">
      {/* 顶部状态栏 */}
      <div className="mobile-status-bar">
        <div className="status-left">
          <span className="app-title">任务管理</span>
        </div>
        <div className="status-right">
          <Badge count={3} size="small">
            <BellOutlined className="notification-icon" />
          </Badge>
          <MenuOutlined className="menu-icon" />
        </div>
      </div>

      {/* 主要内容区域 */}
      <div className="mobile-content">
        {children}
      </div>

      {/* 底部导航栏 */}
      <div className="mobile-bottom-nav">
        {navItems.map(item => (
          <div
            key={item.key}
            className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
            onClick={() => navigate(item.path)}
          >
            <div className="nav-icon">{item.icon}</div>
            <div className="nav-label">{item.label}</div>
          </div>
        ))}
      </div>

      {/* 浮动添加按钮 */}
      <FloatButton
        icon={<PlusOutlined />}
        type="primary"
        onClick={handleAddTask}
        className="mobile-fab"
        tooltip="添加任务"
      />

      {/* 新建任务模态框 */}
      <TaskFormModal
        visible={taskModalVisible}
        onCancel={() => setTaskModalVisible(false)}
        onSuccess={handleTaskSuccess}
      />
    </div>
  );
};

export default MobileLayout;