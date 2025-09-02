// 通用API响应类型
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface Task {
  id?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  dueDate?: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW' | number;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | number;
  category?: string;
  tags?: string[];
  createTime?: string;
  updateTime?: string;
  isAllDay?: boolean;
  repeatType?: string;
  completed?: boolean;
  deleted?: boolean;
  userId?: number;
  parentId?: number;
  subtasks?: Task[];
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