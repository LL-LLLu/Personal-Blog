module.exports = {
  env: {
    node: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
  ],
  rules: {
    // override/add rules settings here, such as:
    'no-unused-vars': 'warn', // Treat unused vars as warnings, not errors
    'vue/no-unused-vars': 'warn',
    'no-undef': 'error', // Keep undefined variables as errors (critical)
    'vue/no-v-html': 'off', // Allow v-html
    'vue/multi-word-component-names': 'off', // Turn off multi-word requirement for simplicity
  }
}