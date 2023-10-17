export function isUrl(url: string) {
  return url.startsWith('https://') || url.startsWith('http://')
}
