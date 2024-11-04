import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private webSocket!: WebSocket;
  public messages: string[] = [];

  constructor() {
    this.connect();
  }

  private connect() {
    this.webSocket = new WebSocket('ws://localhost:2580/stocks');

    this.webSocket.onmessage = (event) => {
      const message = event.data;
      this.messages.push(message);
      console.log("Received:", message);
    };

    this.webSocket.onerror = (error) => {
      console.error("WebSocket error:", error);
    };
  }

  sendMessage(message: string) {
    if (this.webSocket.readyState === WebSocket.OPEN) {
      this.webSocket.send(message);
    } else {
      console.error("WebSocket is not open.");
    }
  }

  disconnect() {
    this.webSocket.close();
  }
}
