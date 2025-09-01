import React from 'react';
import { Card, Button, Tag, Space } from 'antd';
import { 
  EditOutlined, 
  DeleteOutlined, 
  CheckOutlined,
  ClockCircleOutlined,
  FlagOutlined
} from '@ant-design/icons';
import { Task } from '../../types/Task';
import dayjs from 'dayjs';
import './MobileTaskCard.css';

interface MobileTaskCardProps {
  task: Task;
  onEdit: (task: Task) => void;
  onDelete: (taskId: number) => void;
  onToggleComplete: (taskId: number) => void;
}

const MobileTaskCard: React.FC<MobileTaskCardProps> = ({
  task,
  onEdit,
  onDelete,
  onToggleComplete
}) => {
  const isCompleted = task.status === 'COMPLETED' || task.status === 2;
  
  const getPriorityColor = (priority: string | number) => {
    if (priority === 'HIGH' || priority === 3) return '#ff4d4f';
    if (priority === 'MEDIUM' || priority === 2) return '#faad14';
    return '#52c41a';
  };

  const getPriorityText = (priority: string | number) => {
    if (priority === 'HIGH' || priority === 3) return '高';
    if (priority === 'MEDIUM' || priority === 2) return '中';
    return '低';
  };

  const formatDate = (dateString: string) => {
    return dayjs(dateString).format('MM-DD HH:mm');
  };

  return (
    <Card 
      className={`mobile-task-card ${isCompleted ? 'completed' : ''}`}
      size="small"
    >
      <div className="task-header">
        <div className="task-title-section">
          <h4 className={`task-title ${isCompleted ? 'completed-text' : ''}`}>
            {task.title}
          </h4>
          <div className="task-meta">
            <Tag 
              color={getPriorityColor(task.priority)} 
              icon={<FlagOutlined />}
            >
              {getPriorityText(task.priority)}
            </Tag>
            {task.category && (
              <Tag color="blue">{task.category}</Tag>
            )}
          </div>
        </div>
        <Button
          type={isCompleted ? "default" : "primary"}
          shape="circle"
          size="small"
          icon={<CheckOutlined />}
          onClick={() => onToggleComplete(task.id)}
          className={`complete-btn ${isCompleted ? 'completed' : ''}`}
        />
      </div>

      {task.description && (
        <div className={`task-description ${isCompleted ? 'completed-text' : ''}`}>
          {task.description}
        </div>
      )}

      <div className="task-footer">
        <div className="task-time">
          <ClockCircleOutlined className="time-icon" />
          <span className="time-text">
            {task.endTime ? formatDate(task.endTime) : '无截止时间'}
          </span>
        </div>
        
        <Space size="small">
          <Button
            type="text"
            size="small"
            icon={<EditOutlined />}
            onClick={() => onEdit(task)}
            className="action-btn"
          />
          <Button
            type="text"
            size="small"
            icon={<DeleteOutlined />}
            onClick={() => onDelete(task.id)}
            className="action-btn delete-btn"
            danger
          />
        </Space>
      </div>

      {task.tags && task.tags.length > 0 && (
        <div className="task-tags">
          {Array.isArray(task.tags) ? 
            task.tags.map((tag: string, index: number) => (
              <Tag key={index} className="task-tag">
                {tag}
              </Tag>
            )) :
            <Tag className="task-tag">{task.tags}</Tag>
          }
        </div>
      )}
    </Card>
  );
};

export default MobileTaskCard;