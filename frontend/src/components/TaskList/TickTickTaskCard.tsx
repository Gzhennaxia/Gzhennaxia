
import React from 'react';
import { Task } from '../../types/Task';
import { ClockCircleOutlined, FlagOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

interface TickTickTaskCardProps {
  task: Task;
  onEdit?: (task: Task) => void;
  onDelete?: (id: number) => void;
  onToggleComplete?: (id: number) => void;
}

const TickTickTaskCard: React.FC<TickTickTaskCardProps> = ({
  task,
  onEdit,
  onDelete,
  onToggleComplete
}) => {
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const taskDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    
    if (taskDate.getTime() === today.getTime()) {
      return '今天';
    } else if (taskDate.getTime() === today.getTime() + 24 * 60 * 60 * 1000) {
      return '明天';
    } else if (taskDate.getTime() === today.getTime() - 24 * 60 * 60 * 1000) {
      return '昨天';
    } else {
      return date.toLocaleDateString('zh-CN', { 
        month: 'short', 
        day: 'numeric',
        weekday: 'short'
      });
    }
  };

  const isOverdue = (dateString: string) => {
    const dueDate = new Date(dateString);
    const now = new Date();
    return dueDate < now && task.status !== 'COMPLETED';
  };

  const getPriorityColor = (priority: string | number) => {
    const priorityValue = typeof priority === 'string' ? priority : priority.toString();
    switch (priorityValue) {
      case 'HIGH':
      case '3':
        return '#fa541c';
      case 'MEDIUM':
      case '2':
        return '#fa8c16';
      case 'LOW':
      case '1':
        return '#52c41a';
      default:
        return '#d9d9d9';
    }
  };

  return (
    <div className={`task-card fade-in ${task.status === 'COMPLETED' ? 'completed' : ''}`}>
      <div className="task-header">
        <div 
          className={`task-checkbox ${task.status === 'COMPLETED' ? 'checked' : ''}`}
          onClick={() => task.id && onToggleComplete?.(task.id)}
        />
        <div className="task-content">
          <h3 className="task-title">{task.title}</h3>
          {task.description && (
            <p className="task-description">{task.description}</p>
          )}
          <div className="task-meta">
            <span className={`task-priority ${typeof task.priority === 'string' ? task.priority.toLowerCase() : `priority-${task.priority}`}`}>
              <FlagOutlined style={{ color: getPriorityColor(task.priority) }} />
              {task.priority === 'HIGH' || task.priority === 3 ? '高优先级' : 
               task.priority === 'MEDIUM' || task.priority === 2 ? '中优先级' : '低优先级'}
            </span>
            
            {task.endTime && (
              <span className={`task-due-date ${isOverdue(task.endTime) ? 'overdue' : ''}`}>
                <ClockCircleOutlined />
                {formatDate(task.endTime)}
              </span>
            )}
            
            <span className={`task-status ${typeof task.status === 'string' ? task.status.toLowerCase().replace('_', '-') : `status-${task.status}`}`}>
              {(task.status === 'PENDING' || task.status === 0) ? '待处理' :
               (task.status === 'IN_PROGRESS' || task.status === 1) ? '进行中' : '已完成'}
            </span>
          </div>
        </div>
      </div>
      
      <div className="task-actions" style={{
        position: 'absolute',
        top: '16px',
        right: '16px',
        display: 'flex',
        gap: '8px',
        opacity: 0,
        transition: 'opacity 0.2s ease'
      }}>
        <button 
          className="btn-icon"
          onClick={() => onEdit?.(task)}
          style={{
            background: 'none',
            border: 'none',
            padding: '4px',
            borderRadius: '4px',
            cursor: 'pointer',
            color: '#7b8794'
          }}
        >
          <EditOutlined />
        </button>
        <button 
          className="btn-icon"
          onClick={() => task.id && onDelete?.(task.id)}
          style={{
            background: 'none',
            border: 'none',
            padding: '4px',
            borderRadius: '4px',
            cursor: 'pointer',
            color: '#ff4d4f'
          }}
        >
          <DeleteOutlined />
        </button>
      </div>
      

    </div>
  );
};

export default TickTickTaskCard;