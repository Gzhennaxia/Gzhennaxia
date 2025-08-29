import React from 'react';
import { Card, Tag, Button, Space, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, ClockCircleOutlined, CalendarOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { Task } from '../../types/Task';

const { Meta } = Card;

interface ModernTaskCardProps {
  task: Task;
  onEdit: (task: Task) => void;
  onDelete: (id: number) => void;
}

const ModernTaskCard: React.FC<ModernTaskCardProps> = ({ task, onEdit, onDelete }) => {
  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return 'red';
      case 'MEDIUM': return 'orange';
      case 'LOW': return 'green';
      default: return 'default';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'green';
      case 'IN_PROGRESS': return 'blue';
      case 'PENDING': return 'orange';
      case 'CANCELLED': return 'red';
      default: return 'default';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'COMPLETED': return '✅ 已完成';
      case 'IN_PROGRESS': return '🔄 进行中';
      case 'PENDING': return '⏳ 待处理';
      case 'CANCELLED': return '❌ 已取消';
      default: return status;
    }
  };

  const getPriorityText = (priority: string) => {
    switch (priority) {
      case 'HIGH': return '🔥 高优先级';
      case 'MEDIUM': return '⚡ 中优先级';
      case 'LOW': return '🌱 低优先级';
      default: return priority;
    }
  };

  const getPriorityGradient = (priority: string) => {
    switch (priority) {
      case 'HIGH': return 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)';
      case 'MEDIUM': return 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)';
      case 'LOW': return 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)';
      default: return 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
    }
  };

  return (
    <Card
      className={`modern-task-card priority-${typeof task.priority === 'string' ? task.priority.toLowerCase() : task.priority} ${
        task.status === 'COMPLETED' ? 'status-completed' : ''
      }`}
      style={{
        position: 'relative',
        overflow: 'hidden',
        transform: 'translateY(0)',
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
      }}
      bodyStyle={{ padding: '20px' }}
      actions={[
        <Button
          type="text"
          icon={<EditOutlined />}
          onClick={() => onEdit(task)}
          style={{
            color: '#667eea',
            transition: 'all 0.3s ease'
          }}
        />,
        <Popconfirm
          title="确定要删除这个任务吗？"
          onConfirm={() => onDelete(task.id!)}
          okText="确定"
          cancelText="取消"
        >
          <Button 
            type="text" 
            icon={<DeleteOutlined />} 
            danger 
            style={{
              transition: 'all 0.3s ease'
            }}
          />
        </Popconfirm>,
      ]}
    >
      {/* 优先级指示条 */}
      <div style={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        height: '4px',
        background: getPriorityGradient(task.priority),
        borderRadius: '20px 20px 0 0'
      }} />
      
      <Meta
        title={
          <div style={{
            fontSize: '16px',
            fontWeight: '600',
            color: '#2c3e50',
            marginBottom: '8px',
            lineHeight: '1.4'
          }}>
            {task.title}
          </div>
        }
        description={
          <div>
            <p style={{ 
              marginBottom: '12px', 
              color: '#5a6c7d',
              fontSize: '14px',
              lineHeight: '1.5'
            }}>
              {task.description}
            </p>
            
            <Space wrap style={{ marginBottom: '12px' }}>
              <Tag 
                color={getStatusColor(task.status)}
                style={{
                  borderRadius: '12px',
                  padding: '2px 8px',
                  fontSize: '12px',
                  fontWeight: '500'
                }}
              >
                {getStatusText(task.status)}
              </Tag>
              <Tag 
                color={getPriorityColor(task.priority)}
                style={{
                  borderRadius: '12px',
                  padding: '2px 8px',
                  fontSize: '12px',
                  fontWeight: '500'
                }}
              >
                {getPriorityText(task.priority)}
              </Tag>
              {task.category && (
                <Tag style={{
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  color: 'white',
                  border: 'none',
                  borderRadius: '12px',
                  padding: '2px 8px',
                  fontSize: '12px',
                  fontWeight: '500'
                }}>
                  📁 {task.category}
                </Tag>
              )}
            </Space>
            
            <div style={{ 
              fontSize: '12px', 
              color: '#8492a6',
              background: 'rgba(102, 126, 234, 0.1)',
              padding: '8px 12px',
              borderRadius: '8px',
              border: '1px solid rgba(102, 126, 234, 0.2)'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: '4px' }}>
                <CalendarOutlined style={{ marginRight: '6px', color: '#667eea' }} />
                开始: {dayjs(task.startTime).format('MM-DD HH:mm')}
              </div>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <ClockCircleOutlined style={{ marginRight: '6px', color: '#667eea' }} />
                结束: {dayjs(task.endTime).format('MM-DD HH:mm')}
              </div>
            </div>
          </div>
        }
      />
    </Card>
  );
};

export default ModernTaskCard;