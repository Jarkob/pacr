import { Pipe, PipeTransform } from '@angular/core';

/*
 * Put braces around text.
 * Usage:
 *   value | braces
 * Example:
 *   {{ test | braces }}
 *   formats to: (test)
*/
@Pipe({name: 'braces'})
export class BrachesPipe implements PipeTransform {
  transform(value: string): string {
    return '(' + value + ')';
  }
}
