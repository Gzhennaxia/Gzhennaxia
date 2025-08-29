import React, { useState } from 'react';
import { PlusOutlined, HomeOutlined, CheckSquareOutlined, CalendarOutlined, SettingOutlined, MenuOutlined } from '@ant-design/icons';
import TaskFormModal from '../TaskForm/TaskFormModal';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [taskModalVisible, setTaskModalVisible] = useState(false);
  const [sidebarVisible, setSidebarVisible] = useState(false);

  const handleAddTask = () => {
    setTaskModalVisible(true);
  };

  const handleTaskSuccess = () => {
    setTaskModalVisible(false);
    // 刷新页面数据
    window.location.reload();
  };

  return (
    <div className="app">
      {/* 顶部导航栏 */}
      <header className="top-nav">
        <div className="nav-brand">
          <div className="logo">G</div>
          <span>个人管理</span>
        </div>
        <div className="nav-actions">
          <button className="btn btn-primary" onClick={handleAddTask}>
            <PlusOutlined />
            <span className="btn-text">新建任务</span>
          </button>
        </div>
      </header>

      {/* 主要内容区域 */}
      <div className="main-content">
        {/* 内容区域 */}
        <main className="content-area">
          <div className="page-header">
            <h1 className="page-title">任务管理</h1>
            <p className="page-subtitle">管理您的日常任务和待办事项</p>
          </div>
          {children}
        </main>
      </div>

      {/* 移动端浮动新增按钮 */}
      <button 
        className="floating-add-btn"
        onClick={handleAddTask}
        aria-label="新建任务"
      >
        <PlusOutlined />
      </button>

      {/* 新建任务模态框 */}
      <TaskFormModal
        visible={taskModalVisible}
        onCancel={() => setTaskModalVisible(false)}
        onSuccess={handleTaskSuccess}
      />
    </div>
  );
};

export default Layout;