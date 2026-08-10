import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatMessage } from './chat.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements AfterViewChecked {
  messages: ChatMessage[] = [
    {
      role: 'assistant',
      content:
        "Hi, I'm your AI career assistant. Ask me to find jobs, match a resume, or track an application — for Phase 1 I'm just here to chat while the rest of the pipeline gets wired up.",
    },
  ];

  draft = '';
  isSending = false;
  errorMessage = '';

  @ViewChild('scrollAnchor') private scrollAnchor?: ElementRef<HTMLDivElement>;

  constructor(private chatService: ChatService) {}

  ngAfterViewChecked(): void {
    this.scrollAnchor?.nativeElement.scrollIntoView({ behavior: 'smooth' });
  }

  send(): void {
    const text = this.draft.trim();
    if (!text || this.isSending) {
      return;
    }

    this.errorMessage = '';
    const history = [...this.messages];
    this.messages.push({ role: 'user', content: text });
    this.draft = '';
    this.isSending = true;

    this.chatService.sendMessage(text, history).subscribe({
      next: (res) => {
        this.messages.push({ role: 'assistant', content: res.reply });
        this.isSending = false;
      },
      error: () => {
        this.errorMessage =
          "Couldn't reach the assistant. Check that the backend and AI service are running.";
        this.isSending = false;
      },
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }
}
