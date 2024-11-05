import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class CustomErrorHandler implements ErrorHandler {
  handleError(error: any): void {
    if (error && error.message && error.message.includes('NG0900')) {
      return;
    }
    console.error('Произошла ошибка:', error);
  }
}
