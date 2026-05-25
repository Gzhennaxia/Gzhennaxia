import React, { useCallback, useMemo, useState } from 'react';
import { Layout, Menu, Breadcrumb, Button, theme } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import BackToHome from '../Home/BackToHome';
import TransactionFormModal, { TransactionFormType } from './TransactionFormModal';
import { AccountingActionsContext } from './AccountingActionsContext';
import {
  DashboardOutlined,
  UnorderedListOutlined,
  PlusOutlined,
  CreditCardOutlined,
  BarChartOutlined,
  TagsOutlined,
  AppstoreOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const AccountingLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [addOpen, setAddOpen] = useState(false);
  const [addDefaultType, setAddDefaultType] = useState<TransactionFormType>('expense');
  const [refreshKey, setRefreshKey] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const openAddTransaction = useCallback((defaultType?: TransactionFormType) => {
    setAddDefaultType(defaultType ?? 'expense');
    setAddOpen(true);
  }, []);

  const actionsValue = useMemo(
    () => ({ openAddTransaction }),
    [openAddTransaction],
  );

  const menuItems = [
    { key: '/accounting/dashboard', icon: <DashboardOutlined />, label: '总览' },
    { key: '/accounting/transactions', icon: <UnorderedListOutlined />, label: '流水' },
    { key: '/accounting/accounts', icon: <CreditCardOutlined />, label: '账户' },
    { key: '/accounting/categories', icon: <AppstoreOutlined />, label: '分类管理' },
    { key: '/accounting/tags', icon: <TagsOutlined />, label: '标签管理' },
    { key: '/accounting/reports', icon: <BarChartOutlined />, label: '月报' },
  ];

  return (
    <AccountingActionsContext.Provider value={actionsValue}>
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
          <BackToHome />
          <div style={{ marginLeft: 'auto', paddingRight: 24 }}>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => openAddTransaction()}>
              记一笔
            </Button>
          </div>
        </Header>
        <Content style={{ margin: '0 16px' }}>
          <Breadcrumb style={{ margin: '16px 0' }} items={[{ title: '个人记账' }]} />
          <div style={{ padding: 24, minHeight: 360, background: colorBgContainer, borderRadius: borderRadiusLG }}>
            <Outlet key={refreshKey} />
          </div>
        </Content>
      </Layout>
    </Layout>
    <TransactionFormModal
      open={addOpen}
      defaultType={addDefaultType}
      onClose={() => setAddOpen(false)}
      onSuccess={() => setRefreshKey((k) => k + 1)}
    />
    </AccountingActionsContext.Provider>
  );
};

export default AccountingLayout;
