import { Pipe, PipeTransform } from '@angular/core';

/*
 * Transforms an enum value to a more readable string
 * Usage:
 *   value | enumString
 * Example:
 *   {{ TEST_ENUM | enumString }}
 *   formats to: test enum
*/
@Pipe({name: 'enumString'})
export class EnumPipe implements PipeTransform {
  transform(value: string): string {
    let output = value.split('_').join(' ');

    output = output.toLowerCase();

    return output;
  }
}
