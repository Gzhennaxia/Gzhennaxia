import React, { useState } from 'react';
import { Layout, Menu, Breadcrumb, theme } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  UnorderedListOutlined,
  PlusCircleOutlined,
  CreditCardOutlined,
  LineChartOutlined,
  FundOutlined,
  SwapOutlined,
  BarChartOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const AccountingLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const menuItems = [
    { key: '/accounting/dashboard', icon: <DashboardOutlined />, label: '总览' },
    { key: '/accounting/transactions', icon: <UnorderedListOutlined />, label: '流水' },
    { key: '/accounting/add', icon: <PlusCircleOutlined />, label: '记一笔' },
    { key: '/accounting/accounts', icon: <CreditCardOutlined />, label: '账户' },
    { key: '/accounting/investment', icon: <LineChartOutlined />, label: '投资总览' },
    { key: '/accounting/positions', icon: <FundOutlined />, label: '持仓' },
    { key: '/accounting/investment-trades', icon: <SwapOutlined />, label: '投资交易' },
    { key: '/accounting/reports', icon: <BarChartOutlined />, label: '月报' },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div
          style={{
            height: 32,
            margin: 16,
            background: 'rgba(255, 255, 255, 0.2)',
            borderRadius: 6,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontWeight: 'bold',
          }}
        >
          {collapsed ? '账' : '个人记账'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer, display: 'flex', alignItems: 'center' }}>
          <div style={{ fontSize: 16, padding: '0 24px', cursor: 'pointer' }} onClick={() => setCollapsed(!collapsed)}>
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>
        </Header>
        <Content style={{ margin: '0 16px' }}>
          <Breadcrumb style={{ margin: '16px 0' }} items={[{ title: '个人记账' }]} />
          <div style={{ padding: 24, minHeight: 360, background: colorBgContainer, borderRadius: borderRadiusLG }}>
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default AccountingLayout;
