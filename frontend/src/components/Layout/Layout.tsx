import React, { useState } from 'react';
import { Layout as AntLayout, Menu, Button, Drawer } from 'antd';
import { 
  CalendarOutlined, 
  UnorderedListOutlined, 
  PlusOutlined,
  MenuOutlined
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import TaskFormModal from '../TaskForm/TaskFormModal';
import ModernBackground from './ModernBackground';
import { useResponsive } from '../../hooks/useResponsive';

const { Header, Content } = AntLayout;

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [taskModalVisible, setTaskModalVisible] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { isMobile } = useResponsive();

  const menuItems = [
    {
      key: '/',
      icon: <UnorderedListOutlined />,
      label: '任务列表',
    },
    {
      key: '/calendar',
      icon: <CalendarOutlined />,
      label: '日历视图',
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
    setDrawerVisible(false);
  };

  return (
    <div>
      <ModernBackground />
      <AntLayout style={{ minHeight: '100vh', background: 'transparent' }}>
      <Header style={{ 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'space-between',
        padding: isMobile ? '0 16px' : '0 24px',
        background: 'rgba(255, 255, 255, 0.1)',
        backdropFilter: 'blur(20px)',
        border: 'none',
        borderBottom: '1px solid rgba(255, 255, 255, 0.2)',
        boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.37)',
        position: 'sticky',
        top: 0,
        zIndex: 1000
      }}>
        <div style={{ 
          color: 'white', 
          fontSize: isMobile ? '18px' : '24px', 
          fontWeight: '700',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
          flex: 1,
          fontFamily: 'Inter, sans-serif'
        }}>
          {isMobile ? '✨ 时间管理' : '✨ 个人时间管理系统'}
        </div>
        
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {/* 新建任务按钮 */}
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={() => setTaskModalVisible(true)}
            size={isMobile ? 'middle' : 'middle'}
            style={{ 
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              border: 'none',
              borderRadius: '12px',
              fontWeight: '500',
              boxShadow: '0 4px 15px 0 rgba(116, 79, 168, 0.75)',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
            }}
          >
            {!isMobile && '✨ 新建任务'}
          </Button>
          
          {/* 桌面端菜单 */}
          {!isMobile && (
            <Menu
              theme="dark"
              mode="horizontal"
              selectedKeys={[location.pathname]}
              items={menuItems}
              onClick={handleMenuClick}
              style={{ 
                background: 'transparent', 
                border: 'none',
                minWidth: '200px'
              }}
            />
          )}
          
          {/* 移动端菜单按钮 */}
          {isMobile && (
            <Button
              type="text"
              icon={<MenuOutlined />}
              onClick={() => setDrawerVisible(true)}
              style={{ color: 'white' }}
            />
          )}
        </div>
      </Header>
      
      {/* 移动端抽屉菜单 */}
      <Drawer
        title="菜单"
        placement="right"
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
        width={isMobile ? Math.min(280, window.innerWidth * 0.8) : 280}
      >
        <Menu
          mode="vertical"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
          style={{ border: 'none' }}
        />
      </Drawer>
      
      <Content style={{ 
        padding: isMobile ? '12px' : '24px',
        minHeight: 'calc(100vh - 64px)',
        background: 'transparent'
      }}>
        <div style={{
          background: 'rgba(255, 255, 255, 0.05)',
          backdropFilter: 'blur(10px)',
          padding: isMobile ? '16px' : '24px',
          borderRadius: '20px',
          border: '1px solid rgba(255, 255, 255, 0.1)',
          minHeight: 'calc(100vh - 128px)',
          boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.2)'
        }}>
          {children}
        </div>
      </Content>
      
      <TaskFormModal
        visible={taskModalVisible}
        onCancel={() => setTaskModalVisible(false)}
        onSuccess={() => {
          setTaskModalVisible(false);
          // 刷新页面数据
          window.location.reload();
        }}
      />
      </AntLayout>
    </div>
  );
};

export default Layout;