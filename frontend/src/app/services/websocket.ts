import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private webSocket!: WebSocket;
  private messageSubject = new Subject<string>();

  get messages(): Observable<string> {
    return this.messageSubject.asObservable();
  }

  connect(): void {
    this.webSocket = new WebSocket('ws://localhost:2580/socket');

    this.webSocket.onmessage = (event) => {
      this.messageSubject.next(event.data);
    };

    this.webSocket.onerror = (error) => {
      console.error("WebSocket error:", error);
    };
  }

  disconnect(): void {
    if (this.webSocket) {
      this.webSocket.close();
    }
  }
}
