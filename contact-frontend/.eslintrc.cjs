module.exports = {
  root: true,
  env: {
    'vue/setup-compiler-macros': true,
    node: true,
  },
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    sourceType: 'module',
  },
  'plugins': [
    '@typescript-eslint',
  ],
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'plugin:prettier/recommended',
    'plugin:@typescript-eslint/recommended',
  ],
  rules: {
    '@typescript-eslint/no-unused-vars': 'error',
    'no-empty-pattern': 'error',
    'no-useless-escape': 'error',
    'prettier/prettier': [
      'error',
      {
        useTabs: false,
        tabWidth: 2,
        singleQuote: true,
        trailingComma: 'es5',
        printWidth: 80,
        semi: false,
        spacedComment: true
      },
    ],
    'vue/no-mutating-props': 'error',
    'vue/no-unused-components': 'error',
    'vue/no-useless-template-attributes': 'error',
    '@typescript-eslint/no-explicit-any': 'off',
    'vue/multi-word-component-names': 'off',
  },
  ignorePatterns: [
    'node_modules',
    'build',
    'dist',
    'public',
    '*.d.ts',
    '*.cjs',
  ],
  overrides: [
    {
      files: ['./*.js'],
      env: {
        node: true,
      },
    },
  ],
}
