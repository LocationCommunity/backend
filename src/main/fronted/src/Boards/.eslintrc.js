// .eslintrc.js
module.exports = {
    env: {
      browser: true,
      es2021: true,
    },
    extends: [
      'eslint:recommended',
      'plugin:react/recommended',
    ],
    parserOptions: {
      ecmaFeatures: {
        jsx: true,
      },
      ecmaVersion: 12,
      sourceType: 'module',
    },
    plugins: [
      'react',
    ],
    rules: {
      // 프로젝트에 맞게 ESLint 규칙을 설정합니다.
      // 여기에 추가적인 설정을 넣으세요.
      'no-undef': 'off', // 'no-undef' 규칙을 끄고,
      'react/prop-types': 'off', // React 프로퍼티 타입 체크를 끕니다.
    },
    globals: {
      naver: true, // naver를 전역 변수로 인식합니다.
    },
  };
  