export interface Task {
  id?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  dueDate?: string; // 添加dueDate字段
  priority: 'HIGH' | 'MEDIUM' | 'LOW' | number;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | number;
  category?: string;
  tags?: string;
  createTime?: string;
  updateTime?: string;
}

export interface TaskFormData {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  dueDate?: string;
  priority: number | 'HIGH' | 'MEDIUM' | 'LOW';
  status?: string | number;
  category?: string;
  tags?: string[] | string;
  isAllDay?: boolean;
  repeatType?: string;
  id?: number;
  createTime?: string;
  updateTime?: string;
}

export type ViewType = 'day' | 'week' | 'month' | 'multiDay' | 'multiWeek';