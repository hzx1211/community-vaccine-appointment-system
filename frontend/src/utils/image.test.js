import { describe, expect, it } from 'vitest'
import { resolveImageSrc } from './image'

describe('resolveImageSrc', () => {
  it('keeps a usable API image address', () => {
    expect(resolveImageSrc(' /uploads/2026/01/demo.png ', 'vaccine'))
      .toBe('/uploads/2026/01/demo.png')
  })

  it('uses a type-specific fallback for missing values', () => {
    expect(resolveImageSrc('', 'community')).toBe('/default-community.svg')
    expect(resolveImageSrc(null, 'vaccine')).toBe('/default-vaccine.svg')
  })

  it('falls back safely for an unknown type', () => {
    expect(resolveImageSrc(undefined, 'unknown')).toBe('/default-news.svg')
  })
})
