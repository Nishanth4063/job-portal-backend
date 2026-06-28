export interface Job {
  id: number;
  title: string;
  description: string;
  location: string;
  companyName?: string;
  salary?: number;
  postedByUserId?: number;
  createdAt?: Date;
}