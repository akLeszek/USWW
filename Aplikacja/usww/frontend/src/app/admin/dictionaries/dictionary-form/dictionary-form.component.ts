import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Dictionary, DictionaryService } from '../../../shared/services/dictionary.service';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'app-dictionary-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './dictionary-form.component.html',
  styleUrls: ['./dictionary-form.component.scss']
})
export class DictionaryFormComponent implements OnInit {
  dictionaryForm: FormGroup;
  dictionaryType = '';
  dictionaryId: number | null = null;
  isEditMode = false;
  loading = false;
  error = '';
  success = '';

  constructor(
    private formBuilder: FormBuilder,
    private dictionaryService: DictionaryService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.dictionaryForm = this.formBuilder.group({
      idn: ['', [Validators.required, Validators.maxLength(15), Validators.pattern(/^[A-Z0-9_]+$/)]],
      name: ['', [Validators.required, Validators.maxLength(255)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.dictionaryType = params.get('type') || '';
      const id = params.get('id');

      if (id && id !== 'new') {
        this.dictionaryId = parseInt(id, 10);
        this.isEditMode = true;
        this.loadDictionary();
      }
    });
  }

  loadDictionary(): void {
    if (!this.dictionaryId) return;

    this.loading = true;
    let observable: Observable<Dictionary>;

    switch (this.dictionaryType) {
      case 'categories':
        observable = this.dictionaryService.getTicketCategories()
          .pipe(map(categories => categories.find(c => c.id === this.dictionaryId) as Dictionary));
        break;
      case 'statuses':
        observable = this.dictionaryService.getTicketStatuses()
          .pipe(map(statuses => statuses.find(s => s.id === this.dictionaryId) as Dictionary));
        break;
      case 'priorities':
        observable = this.dictionaryService.getTicketPriorities()
          .pipe(map(priorities => priorities.find(p => p.id === this.dictionaryId) as Dictionary));
        break;
      default:
        this.error = 'Nieznany typ słownika';
        this.loading = false;
        return;
    }

    observable.subscribe({
      next: (dictionary) => {
        if (dictionary) {
          this.dictionaryForm.patchValue({
            idn: dictionary.idn,
            name: dictionary.name
          });
        } else {
          this.error = 'Nie znaleziono elementu słownika';
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Nie udało się załadować elementu słownika';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.dictionaryForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const dictionaryData: Dictionary = {
      id: this.isEditMode ? this.dictionaryId as number : undefined,
      idn: this.dictionaryForm.value.idn,
      name: this.dictionaryForm.value.name
    };

    let observable: Observable<Dictionary>;

    if (this.isEditMode && this.dictionaryId) {
      // Update
      switch (this.dictionaryType) {
        case 'categories':
          observable = this.dictionaryService.updateTicketCategory(this.dictionaryId, dictionaryData);
          break;
        case 'statuses':
          observable = this.dictionaryService.updateTicketStatus(this.dictionaryId, dictionaryData);
          break;
        case 'priorities':
          observable = this.dictionaryService.updateTicketPriority(this.dictionaryId, dictionaryData);
          break;
        default:
          this.error = 'Nieznany typ słownika';
          this.loading = false;
          return;
      }
    } else {
      // Create
      switch (this.dictionaryType) {
        case 'categories':
          observable = this.dictionaryService.createTicketCategory(dictionaryData);
          break;
        case 'statuses':
          observable = this.dictionaryService.createTicketStatus(dictionaryData);
          break;
        case 'priorities':
          observable = this.dictionaryService.createTicketPriority(dictionaryData);
          break;
        default:
          this.error = 'Nieznany typ słownika';
          this.loading = false;
          return;
      }
    }

    observable.subscribe({
      next: (response) => {
        this.success = `Element słownika został ${this.isEditMode ? 'zaktualizowany' : 'utworzony'}`;
        setTimeout(() => {
          this.router.navigate(['/admin/dictionaries', this.dictionaryType]);
        }, 1500);
      },
      error: (error) => {
        this.error = error.error?.message || `Nie udało się ${this.isEditMode ? 'zaktualizować' : 'utworzyć'} elementu słownika`;
        this.loading = false;
      }
    });
  }

  getTitle(): string {
    const action = this.isEditMode ? 'Edycja' : 'Dodawanie';

    switch (this.dictionaryType) {
      case 'categories': return `${action} kategorii zgłoszeń`;
      case 'statuses': return `${action} statusu zgłoszeń`;
      case 'priorities': return `${action} priorytetu zgłoszeń`;
      default: return `${action} elementu słownika`;
    }
  }

  goBack(): void {
    this.router.navigate(['/admin/dictionaries', this.dictionaryType]);
  }
}
