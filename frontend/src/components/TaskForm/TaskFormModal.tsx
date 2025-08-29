import React, { useEffect } from 'react';
import { Modal, Form, Input, DatePicker, Select, message } from 'antd';
import dayjs from 'dayjs';
import { Task } from '../../types/Task';
import { taskService } from '../../services/taskService';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface TaskFormModalProps {
  visible: boolean;
  task?: Task;
  onCancel: () => void;
  onSuccess: () => void;
}

const TaskFormModal: React.FC<TaskFormModalProps> = ({
  visible,
  task,
  onCancel,
  onSuccess,
}) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible && task) {
      // 转换数字优先级为文本
      const priorityTextMap: { [key: string]: string } = {
        '1': 'LOW',
        '2': 'MEDIUM', 
        '3': 'HIGH'
      };
      
      form.setFieldsValue({
        title: task.title,
        description: task.description,
        timeRange: [dayjs(task.startTime), dayjs(task.endTime)],
        priority: priorityTextMap[task.priority.toString()] || 'MEDIUM',
        status: task.status,
        category: task.category,
        tags: Array.isArray(task.tags) ? task.tags.join(', ') : task.tags,
      });
    } else if (visible) {
      form.resetFields();
    }
  }, [visible, task, form]);

  const handleSubmit = async (values: any) => {
    try {
      // 转换优先级文本为数字
      const priorityMap: { [key: string]: number } = {
        'LOW': 1,
        'MEDIUM': 2,
        'HIGH': 3
      };

      const taskData = {
        title: values.title,
        description: values.description,
        startTime: values.timeRange[0].format('YYYY-MM-DDTHH:mm:ss'),
        endTime: values.timeRange[1].format('YYYY-MM-DDTHH:mm:ss'),
        priority: priorityMap[values.priority] || 2,
        category: values.category,
        tags: values.tags ? values.tags.split(',').map((tag: string) => tag.trim()) : [],
        isAllDay: false,
        repeatType: 'none'
      };

      if (task?.id) {
        await taskService.updateTask(task.id, taskData);
        message.success('任务更新成功');
      } else {
        await taskService.createTask(taskData);
        message.success('任务创建成功');
      }
      
      onSuccess();
    } catch (error) {
      message.error('操作失败，请重试');
      console.error('Task operation failed:', error);
    }
  };

  return (
    <Modal
      title={task ? '编辑任务' : '新建任务'}
      open={visible}
      onCancel={onCancel}
      onOk={() => form.submit()}
      okText="确定"
      cancelText="取消"
      width={window.innerWidth <= 768 ? '95%' : 600}
      style={window.innerWidth <= 768 ? { top: 20 } : {}}
      styles={window.innerWidth <= 768 ? { body: { padding: '16px' } } : {}}
      className="task-form-modal"
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{
          priority: 'MEDIUM',
          status: 'PENDING',
        }}
      >
        <Form.Item
          name="title"
          label="任务标题"
          rules={[{ required: true, message: '请输入任务标题' }]}
        >
          <Input placeholder="请输入任务标题" />
        </Form.Item>

        <Form.Item
          name="description"
          label="任务描述"
        >
          <TextArea rows={3} placeholder="请输入任务描述" />
        </Form.Item>

        <Form.Item
          name="timeRange"
          label="时间范围"
          rules={[{ required: true, message: '请选择时间范围' }]}
        >
          <RangePicker
            showTime
            format="YYYY-MM-DD HH:mm"
            placeholder={['开始时间', '结束时间']}
            style={{ width: '100%' }}
          />
        </Form.Item>

        <Form.Item
          name="priority"
          label="优先级"
          rules={[{ required: true, message: '请选择优先级' }]}
        >
          <Select placeholder="请选择优先级">
            <Select.Option value="HIGH">高</Select.Option>
            <Select.Option value="MEDIUM">中</Select.Option>
            <Select.Option value="LOW">低</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="status"
          label="状态"
          rules={[{ required: true, message: '请选择状态' }]}
        >
          <Select placeholder="请选择状态">
            <Select.Option value="PENDING">待处理</Select.Option>
            <Select.Option value="IN_PROGRESS">进行中</Select.Option>
            <Select.Option value="COMPLETED">已完成</Select.Option>
            <Select.Option value="CANCELLED">已取消</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="category"
          label="分类"
        >
          <Input placeholder="请输入分类" />
        </Form.Item>

        <Form.Item
          name="tags"
          label="标签"
        >
          <Input placeholder="请输入标签，多个标签用逗号分隔" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default TaskFormModal;