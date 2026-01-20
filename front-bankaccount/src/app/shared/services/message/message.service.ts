import { Injectable, inject } from '@angular/core';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';

import { GENERAL_CONSTANTS } from '../../static/constants/general.constants';

interface SnackOptions {
  action?: string;
  duration?: number;
  panelClass?: string | string[];
  horizontalPosition?: MatSnackBarHorizontalPosition;
  verticalPosition?: MatSnackBarVerticalPosition;
}

@Injectable({ providedIn: 'root' })
export class MessageService {
  private readonly snackBar = inject(MatSnackBar);
  private readonly defaults = GENERAL_CONSTANTS.SNACKBAR;

  showSuccess(message: string, options: SnackOptions = {}): void {
    this.snackBar.open(message, options.action ?? this.defaults.ACTIONS.SUCCESS_ICON, {
      duration: options.duration ?? this.defaults.DURATION_MS.SUCCESS,
      horizontalPosition: options.horizontalPosition ?? this.defaults.POSITION.HORIZONTAL,
      verticalPosition: options.verticalPosition ?? this.defaults.POSITION.VERTICAL,
      panelClass: options.panelClass ?? this.defaults.PANEL_CLASS.SUCCESS,
    });
  }

  showError(message: string, options: SnackOptions = {}): void {
    this.snackBar.open(message, options.action ?? this.defaults.ACTIONS.ERROR_ICON, {
      duration: options.duration ?? this.defaults.DURATION_MS.ERROR,
      horizontalPosition: options.horizontalPosition ?? this.defaults.POSITION.HORIZONTAL,
      verticalPosition: options.verticalPosition ?? this.defaults.POSITION.VERTICAL,
      panelClass: options.panelClass ?? this.defaults.PANEL_CLASS.ERROR,
    });
  }
}
