export interface Task {
  id?: number;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
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
  priority: number;
  category?: string;
  tags?: string[];
  isAllDay?: boolean;
  repeatType?: string;
}

export type ViewType = 'day' | 'week' | 'month' | 'multiDay' | 'multiWeek';