import React, { useState } from 'react';
import { Layout, Menu, Button, Typography } from 'antd';
import { FileTextOutlined, UploadOutlined, BookOutlined, SettingOutlined } from '@ant-design/icons';
import BackToHome from '../Home/BackToHome';
import PDFUpload from './PDFUpload';
import SimplePDFViewer from './SimplePDFViewer';
import QuestionBankList from './QuestionBankList';
import { PDFDocument } from '../../types/QuestionBank';
import './QuestionBankLayout.css';

const { Sider, Content } = Layout;
const { Title } = Typography;

type ViewType = 'upload' | 'viewer' | 'questions' | 'settings';

const QuestionBankLayout: React.FC = () => {
  const [currentView, setCurrentView] = useState<ViewType>('upload');
  const [selectedDocument, setSelectedDocument] = useState<PDFDocument | null>(null);
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    {
      key: 'upload',
      icon: <UploadOutlined />,
      label: '上传PDF',
    },
    {
      key: 'viewer',
      icon: <FileTextOutlined />,
      label: 'PDF查看器',
    },
    {
      key: 'questions',
      icon: <BookOutlined />,
      label: '题库管理',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
  ];

  const handleMenuClick = (key: string) => {
    setCurrentView(key as ViewType);
  };

  const handleDocumentSelect = (document: PDFDocument) => {
    setSelectedDocument(document);
    setCurrentView('viewer');
  };

  const renderContent = () => {
    switch (currentView) {
      case 'upload':
        return <PDFUpload onDocumentSelect={handleDocumentSelect} />;
      case 'viewer':
        return <SimplePDFViewer document={selectedDocument} />;
      case 'questions':
        return <QuestionBankList />;
      case 'settings':
        return (
          <div style={{ padding: '24px' }}>
            <Title level={3}>设置</Title>
            <p>设置功能开发中...</p>
          </div>
        );
      default:
        return <PDFUpload onDocumentSelect={handleDocumentSelect} />;
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider 
        collapsible 
        collapsed={collapsed} 
        onCollapse={setCollapsed}
        theme="light"
        width={250}
      >
        <div className="question-bank-logo">
          <BookOutlined style={{ fontSize: '24px', color: '#1890ff' }} />
          {!collapsed && <span style={{ marginLeft: '12px', fontSize: '18px', fontWeight: 'bold' }}>题库系统</span>}
        </div>
        <Menu
          mode="inline"
          selectedKeys={[currentView]}
          items={menuItems}
          onClick={({ key }) => handleMenuClick(key)}
          style={{ borderRight: 0 }}
        />
      </Sider>
      <Layout>
        <Content style={{ margin: 0, background: '#f0f2f5' }}>
          <div style={{ padding: '8px 16px', background: '#fff', borderBottom: '1px solid #f0f0f0' }}>
            <BackToHome />
          </div>
          {renderContent()}
        </Content>
      </Layout>
    </Layout>
  );
};

export default QuestionBankLayout;