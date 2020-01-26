import { Pipe, PipeTransform } from '@angular/core';

/*
 * Shortens a string to x characters
 * Usage:
 *   value | shortenString:x
 * Example:
 *   {{ 123456789 | shortenString:7 }}
 *   formats to: 1234567
*/
@Pipe({name: 'shortenString'})
export class ShortenStringPipe implements PipeTransform {
  transform(value: string, amt?: number): string {
    return value.substr(0, amt);
  }
}
