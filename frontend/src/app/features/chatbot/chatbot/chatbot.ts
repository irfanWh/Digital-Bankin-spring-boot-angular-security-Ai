import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatResponse, ChatbotService } from '../../../core/api/chatbot.service';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  createdAt?: string;
}

@Component({
  selector: 'app-chatbot',
  imports: [FormsModule],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.css',
})
export class Chatbot implements OnInit {
  private readonly chatbotService = inject(ChatbotService);

  messages: ChatMessage[] = [];
  message = '';
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.chatbotService.history().subscribe({
      next: (history) => {
        this.messages = history.map((item) => this.toAssistantMessage(item));
      },
    });
  }

  send(): void {
    const content = this.message.trim();
    if (!content || this.loading) {
      return;
    }

    this.messages = [...this.messages, { role: 'user', content }];
    this.message = '';
    this.loading = true;
    this.errorMessage = '';

    this.chatbotService.ask({ message: content }).subscribe({
      next: (response) => {
        this.messages = [...this.messages, this.toAssistantMessage(response)];
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Le chatbot est indisponible';
        this.loading = false;
      },
    });
  }

  private toAssistantMessage(response: ChatResponse): ChatMessage {
    return {
      role: 'assistant',
      content: response.answer,
      createdAt: response.createdAt,
    };
  }
}
