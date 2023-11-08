export function compareStringsIgnoringCase(str1: string, str2: string): number {
    const str1lc = str1.toLowerCase();
    const str2lc = str2.toLowerCase();
    if (str1lc < str2lc) return -1;
    if (str1lc > str2lc) return +1;
    if (str1 < str2) return -1;
    if (str1 > str2) return +1;
    return 0;
}

export function getWordCount(answer: string) {
    const words = answer.split(/\s+/);
    return words.length;
}

export function trimLongText(text: string, maxLength: number) {
    if (!text || text.length <= maxLength - 3) return text;
    return text.substring(0, maxLength - 3) + "..."
}
