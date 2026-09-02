import DOMPurify from 'dompurify'

const SANITIZE_OPTIONS = {
  USE_PROFILES: { html: true },
  FORBID_TAGS: ['style', 'iframe', 'object', 'embed', 'form', 'input', 'button'],
  FORBID_ATTR: ['style']
}

/**
 * 富文本内容来自数据库时，先过滤掉脚本、内嵌页面和事件属性再交给 v-html。
 */
export function sanitizeRichHtml(value, fallback = '') {
  const source = typeof value === 'string' && value.trim() ? value : fallback
  return DOMPurify.sanitize(source, SANITIZE_OPTIONS)
}
