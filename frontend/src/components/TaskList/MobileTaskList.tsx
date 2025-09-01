import React, { useState } from 'react';
import { List, Input, Button, Tabs, FloatButton } from 'antd';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Task } from '../../types/Task';
import LoadingSpinner from '../Common/LoadingSpinner';
import TickTickTaskCard from './TickTickTaskCard';
import './MobileTaskList.css';

interface MobileTaskListProps {
  tasks: Task[];
  loading: boolean;
  onEdit: (task: Task) => void;
  onDelete: (taskId: number) => void;
  onToggleComplete: (taskId: number) => void;
  onRefresh: () => void;
  searchTerm: string;
  onSearchChange: (value: string) => void;
  onQuickAdd: () => void;
  quickTaskTitle: string;
  onQuickTaskTitleChange: (value: string) => void;
}

const MobileTaskList: React.FC<MobileTaskListProps> = ({
  tasks,
  loading,
  onEdit,
  onDelete,
  onToggleComplete,
  onRefresh,
  searchTerm,
  onSearchChange,
  onQuickAdd,
  quickTaskTitle,
  onQuickTaskTitleChange
}: MobileTaskListProps) => {
  const [activeTab, setActiveTab] = useState<string>('all');

  const getTasksByStatus = (status: string): Task[] => {
    switch (status) {
      case 'pending':
        return tasks.filter((task: Task) => task.status === 'PENDING' || task.status === 0);
      case 'completed':
        return tasks.filter((task: Task) => task.status === 'COMPLETED' || task.status === 2);
      case 'all':
      default:
        return tasks;
    }
  };

  const getTaskCount = (status: string): number => {
    return getTasksByStatus(status).length;
  };

  return (
    <div className="mobile-task-list">
      {/* 移动端头部 */}
      <div className="mobile-header">
        <div className="mobile-title">
          <h1>任务管理</h1>
          <p>管理您的日常任务</p>
        </div>
        <Input
          placeholder="搜索任务..."
          prefix={<SearchOutlined />}
          value={searchTerm}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => onSearchChange(e.target.value)}
          allowClear
          className="mobile-search"
        />
      </div>

      {/* 快速添加区域 */}
      <div className="mobile-quick-add">
        <Input
          placeholder="快速添加任务..."
          value={quickTaskTitle}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => onQuickTaskTitleChange(e.target.value)}
          onPressEnter={onQuickAdd}
          suffix={
            <Button 
              type="primary" 
              size="small" 
              onClick={onQuickAdd}
              disabled={!quickTaskTitle.trim()}
            >
              添加
            </Button>
          }
        />
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          className="mobile-tabs"
          items={[
            {
              key: 'all',
              label: `全部 (${getTaskCount('all')})`,
              children: (
                <List
                  dataSource={getTasksByStatus('all')}
                  renderItem={(task) => (
                    <List.Item className="mobile-task-item">
                      <TickTickTaskCard
                        key={task.id}
                        task={task}
                        onEdit={onEdit}
                        onDelete={onDelete}
                        onToggleComplete={onToggleComplete}
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无任务' }}
                />
              ),
            },
            {
              key: 'pending',
              label: `待办 (${getTaskCount('pending')})`,
              children: (
                <List
                  dataSource={getTasksByStatus('pending')}
                  renderItem={(task) => (
                    <List.Item className="mobile-task-item">
                      <TickTickTaskCard
                        key={task.id}
                        task={task}
                        onEdit={onEdit}
                        onDelete={onDelete}
                        onToggleComplete={onToggleComplete}
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无待办任务' }}
                />
              ),
            },
            {
              key: 'completed',
              label: `已完成 (${getTaskCount('completed')})`,
              children: (
                <List
                  dataSource={getTasksByStatus('completed')}
                  renderItem={(task) => (
                    <List.Item className="mobile-task-item">
                      <TickTickTaskCard
                        key={task.id}
                        task={task}
                        onEdit={onEdit}
                        onDelete={onDelete}
                        onToggleComplete={onToggleComplete}
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无已完成任务' }}
                />
              ),
            },
          ]}
        />
      )}

      {/* 浮动添加按钮 */}
      <FloatButton
        icon={<PlusOutlined />}
        type="primary"
        onClick={onQuickAdd}
        className="mobile-add-button"
        tooltip="添加新任务"
      />
    </div>
  );
};

export default MobileTaskList;