export function compareStringsIgnoringCase(str1: string, str2: string): number {
    const str1lc = str1.toLowerCase();
    const str2lc = str2.toLowerCase();
    if (str1lc < str2lc) return -1;
    if (str1lc > str2lc) return +1;
    if (str1 < str2) return -1;
    if (str1 > str2) return +1;
    return 0;
}
