import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IBlog } from '../blog.model';

@Component({
  selector: 'jhi-blog-detail',
  templateUrl: './blog-detail.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class BlogDetailComponent {
  @Input() blog: IBlog | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
