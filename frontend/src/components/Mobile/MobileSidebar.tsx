import { useState, useEffect } from 'react';
import { Drawer, List, Badge, Avatar } from 'antd';
import { 
  CalendarOutlined, 
  InboxOutlined, 
  FolderOutlined, 
  DeleteOutlined,
  UserOutlined 
} from '@ant-design/icons';
import { inboxTaskService } from '../../services/inboxTaskService';
import { taskService } from '../../services/taskService';
import './MobileSidebar.css';

interface MobileSidebarProps {
  visible: boolean;
  onClose: () => void;
  onMenuSelect: (key: string) => void;
}

const MobileSidebar: React.FC<MobileSidebarProps> = ({
  visible,
  onClose,
  onMenuSelect
}) => {
  const [inboxCount, setInboxCount] = useState<number>(0);
  const [taskStats, setTaskStats] = useState({
    today: 0,
    completed: 0,
    overdue: 0
  });

  useEffect(() => {
    if (visible) {
      loadCounts();
    }
  }, [visible]);

  const loadCounts = async () => {
    try {
      const [inboxCountData, statsData] = await Promise.all([
        inboxTaskService.getInboxTaskCount(),
        taskService.getTaskStats()
      ]);
      setInboxCount(inboxCountData);
      setTaskStats(statsData);
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  };

  const menuItems = [
    {
      key: 'today',
      icon: <CalendarOutlined style={{ color: '#1890ff' }} />,
      title: '今天',
      count: taskStats.today,
      color: '#1890ff'
    },
    {
      key: 'inbox',
      icon: <InboxOutlined style={{ color: '#ff7a00' }} />,
      title: '收集箱',
      count: inboxCount,
      color: '#ff7a00'
    },
    {
      key: 'trash',
      icon: <DeleteOutlined style={{ color: '#999' }} />,
      title: '已放弃',
      count: 0,
      color: '#999'
    }
  ];

  const handleMenuClick = (key: string) => {
    onMenuSelect(key);
    onClose();
  };

  return (
    <Drawer
      title={
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <Avatar icon={<UserOutlined />} />
          <span>给联跪下、</span>
        </div>
      }
      placement="left"
      onClose={onClose}
      open={visible}
      width={280}
      styles={{
        body: { padding: 0 },
        header: { 
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          borderBottom: 'none'
        }
      }}
    >
      <List
        dataSource={menuItems}
        renderItem={(item) => (
          <List.Item
            onClick={() => handleMenuClick(item.key)}
            style={{
              cursor: 'pointer',
              padding: '16px 24px',
              borderBottom: '1px solid #f0f0f0'
            }}
            className="sidebar-menu-item"
          >
            <div style={{ display: 'flex', alignItems: 'center', width: '100%' }}>
              <div style={{ marginRight: 16, fontSize: 20 }}>
                {item.icon}
              </div>
              <div style={{ flex: 1, fontSize: 16, fontWeight: 500 }}>
                {item.title}
              </div>
              <Badge 
                count={item.count} 
                style={{ 
                  backgroundColor: item.color,
                  fontSize: 12
                }}
                showZero={false}
              />
            </div>
          </List.Item>
        )}
      />
      
      <div style={{ 
        position: 'absolute', 
        bottom: 24, 
        left: 24, 
        right: 24,
        textAlign: 'center',
        color: '#999',
        fontSize: 12
      }}>
        <div style={{ marginBottom: 8 }}>
          <span>📱 添加</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 24 }}>
          <span>⚙️</span>
        </div>
      </div>
    </Drawer>
  );
};

export default MobileSidebar;