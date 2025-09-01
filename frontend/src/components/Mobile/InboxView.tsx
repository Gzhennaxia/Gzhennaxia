import React, { useState, useEffect } from 'react';
import { List, Button, Input, message, Empty, FloatButton } from 'antd';
import { PlusOutlined, InboxOutlined } from '@ant-design/icons';
import { InboxTask } from '../../types/InboxTask';
import { inboxTaskService } from '../../services/inboxTaskService';
import { taskService } from '../../services/taskService';
import InboxTaskCard from '../TaskList/InboxTaskCard';
import InboxTaskFormModal from '../TaskForm/InboxTaskFormModal';
import TaskFormModal from '../TaskForm/TaskFormModal';
import LoadingSpinner from '../Common/LoadingSpinner';

const InboxView: React.FC = () => {
  const [inboxTasks, setInboxTasks] = useState<InboxTask[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [inboxModalVisible, setInboxModalVisible] = useState<boolean>(false);
  const [scheduleModalVisible, setScheduleModalVisible] = useState<boolean>(false);
  const [editingTask, setEditingTask] = useState<InboxTask | undefined>();
  const [schedulingTask, setSchedulingTask] = useState<InboxTask | undefined>();
  const [quickTaskTitle, setQuickTaskTitle] = useState<string>('');

  useEffect(() => {
    loadInboxTasks();
  }, []);

  const loadInboxTasks = async () => {
    try {
      setLoading(true);
      const data = await inboxTaskService.getAllInboxTasks();
      setInboxTasks(data);
    } catch (error) {
      message.error('加载收集箱任务失败');
      console.error('Load inbox tasks failed:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (task: InboxTask) => {
    setEditingTask(task);
    setInboxModalVisible(true);
  };

  const handleDelete = async (taskId: number) => {
    try {
      await inboxTaskService.deleteInboxTask(taskId);
      message.success('任务删除成功');
      loadInboxTasks();
    } catch (error) {
      message.error('删除任务失败');
      console.error('Delete inbox task failed:', error);
    }
  };

  const handleMoveToSchedule = (task: InboxTask) => {
    setSchedulingTask(task);
    setScheduleModalVisible(true);
  };

  const handleInboxTaskSuccess = () => {
    setInboxModalVisible(false);
    setEditingTask(undefined);
    loadInboxTasks();
  };

  const handleScheduleSuccess = async () => {
    if (schedulingTask?.id) {
      try {
        await inboxTaskService.deleteInboxTask(schedulingTask.id);
        message.success('任务已安排到日程');
        setScheduleModalVisible(false);
        setSchedulingTask(undefined);
        loadInboxTasks();
      } catch (error) {
        message.error('移动任务失败');
        console.error('Move task to schedule failed:', error);
      }
    }
  };

  const handleQuickAdd = async () => {
    if (!quickTaskTitle.trim()) {
      message.warning('请输入任务标题');
      return;
    }

    try {
      await inboxTaskService.createInboxTask({
        title: quickTaskTitle.trim(),
        description: '',
        priority: 'MEDIUM'
      });
      message.success('任务添加成功');
      setQuickTaskTitle('');
      loadInboxTasks();
    } catch (error) {
      message.error('添加任务失败');
      console.error('Quick add inbox task failed:', error);
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div style={{ padding: '16px', paddingBottom: '80px' }}>
      <div style={{ marginBottom: 16 }}>
        <Input
          placeholder="快速添加到收集箱..."
          value={quickTaskTitle}
          onChange={(e) => setQuickTaskTitle(e.target.value)}
          onPressEnter={handleQuickAdd}
          suffix={
            <Button
              type="text"
              icon={<PlusOutlined />}
              onClick={handleQuickAdd}
              disabled={!quickTaskTitle.trim()}
            />
          }
        />
      </div>

      {inboxTasks.length === 0 ? (
        <Empty
          image={<InboxOutlined style={{ fontSize: 64, color: '#d9d9d9' }} />}
          description="收集箱是空的"
          style={{ marginTop: 60 }}
        >
          <Button type="primary" onClick={() => setInboxModalVisible(true)}>
            添加第一个任务
          </Button>
        </Empty>
      ) : (
        <List
          dataSource={inboxTasks}
          renderItem={(task) => (
            <List.Item style={{ padding: 0, border: 'none' }}>
              <InboxTaskCard
                task={task}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onMoveToSchedule={handleMoveToSchedule}
              />
            </List.Item>
          )}
        />
      )}

      <FloatButton
        icon={<PlusOutlined />}
        type="primary"
        onClick={() => setInboxModalVisible(true)}
        style={{ right: 24, bottom: 24 }}
      />

      <InboxTaskFormModal
        visible={inboxModalVisible}
        task={editingTask}
        onCancel={() => {
          setInboxModalVisible(false);
          setEditingTask(undefined);
        }}
        onSuccess={handleInboxTaskSuccess}
      />

      <TaskFormModal
        visible={scheduleModalVisible}
        task={schedulingTask ? {
          title: schedulingTask.title,
          description: schedulingTask.description || '',
          priority: schedulingTask.priority,
          status: 'PENDING'
        } : undefined}
        onCancel={() => {
          setScheduleModalVisible(false);
          setSchedulingTask(undefined);
        }}
        onSuccess={handleScheduleSuccess}
      />
    </div>
  );
};

export default InboxView;