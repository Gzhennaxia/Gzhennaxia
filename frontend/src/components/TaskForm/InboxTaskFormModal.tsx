import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, message } from 'antd';
import { InboxTask } from '../../types/InboxTask';
import { inboxTaskService } from '../../services/inboxTaskService';

const { TextArea } = Input;
const { Option } = Select;

interface InboxTaskFormModalProps {
  visible: boolean;
  task?: InboxTask;
  onCancel: () => void;
  onSuccess: () => void;
}

const InboxTaskFormModal: React.FC<InboxTaskFormModalProps> = ({
  visible,
  task,
  onCancel,
  onSuccess
}) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      if (task) {
        form.setFieldsValue({
          title: task.title,
          description: task.description || '',
          priority: task.priority || 'MEDIUM'
        });
      } else {
        form.resetFields();
        form.setFieldsValue({
          priority: 'MEDIUM'
        });
      }
    }
  }, [visible, task, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const taskData = {
        title: values.title,
        description: values.description || '',
        priority: values.priority
      };

      if (task?.id) {
        await inboxTaskService.updateInboxTask(task.id, taskData);
        message.success('收集箱任务更新成功');
      } else {
        await inboxTaskService.createInboxTask(taskData);
        message.success('收集箱任务创建成功');
      }
      
      onSuccess();
    } catch (error) {
      message.error('操作失败，请重试');
      console.error('InboxTask operation failed:', error);
    }
  };

  return (
    <Modal
      title={task ? '编辑收集箱任务' : '新建收集箱任务'}
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      okText="确定"
      cancelText="取消"
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          priority: 'MEDIUM'
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
          <TextArea
            rows={3}
            placeholder="请输入任务描述（可选）"
          />
        </Form.Item>

        <Form.Item
          name="priority"
          label="优先级"
        >
          <Select>
            <Option value="LOW">低</Option>
            <Option value="MEDIUM">中</Option>
            <Option value="HIGH">高</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default InboxTaskFormModal;