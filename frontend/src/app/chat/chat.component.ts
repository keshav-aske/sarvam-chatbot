import { Component, ElementRef, ViewChild, AfterViewChecked, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ConversationSummary, MessageDto } from '../services/chat.service';

interface UiMessage {
  role: 'user' | 'assistant';
  content: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('scrollAnchor') scrollAnchor!: ElementRef<HTMLDivElement>;

  conversations: ConversationSummary[] = [];
  conversationId: string | null = null;
  messages: UiMessage[] = [];
  input = '';
  sending = false;
  error = '';

  constructor(private chat: ChatService) {}

  ngOnInit(): void {
    this.refreshConversations();
  }

  ngAfterViewChecked(): void {
    if (this.scrollAnchor) {
      this.scrollAnchor.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }
  }

  refreshConversations(): void {
    this.chat.listConversations().subscribe({
      next: (list) => (this.conversations = list),
      error: () => {}
    });
  }

  newChat(): void {
    this.conversationId = null;
    this.messages = [];
    this.error = '';
  }

  openConversation(id: string): void {
    this.conversationId = id;
    this.error = '';
    this.chat.getConversation(id).subscribe({
      next: (msgs: MessageDto[]) => {
        this.messages = msgs
          .filter((m) => m.role !== 'system')
          .map((m) => ({ role: m.role as 'user' | 'assistant', content: m.content }));
      },
      error: () => (this.error = 'Failed to load conversation')
    });
  }

  deleteConversation(id: string, event: Event): void {
    event.stopPropagation();
    this.chat.deleteConversation(id).subscribe({
      next: () => {
        if (this.conversationId === id) {
          this.newChat();
        }
        this.refreshConversations();
      }
    });
  }

  send(): void {
    const text = this.input.trim();
    if (!text || this.sending) return;

    this.messages.push({ role: 'user', content: text });
    this.input = '';
    this.sending = true;
    this.error = '';

    this.chat.send({ conversationId: this.conversationId, message: text }).subscribe({
      next: (res) => {
        this.conversationId = res.conversationId;
        this.messages.push({ role: 'assistant', content: res.reply });
        this.sending = false;
        this.refreshConversations();
      },
      error: (err) => {
        this.error = err?.error?.error || 'Request failed. Is the backend running and SARVAM_API_KEY set?';
        this.sending = false;
      }
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }
}
