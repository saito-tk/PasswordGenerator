# 概要
指定された条件に基づいて強力なパスワードを生成する、iOS/Android両対応のモバイルアプリを開発します。  
ユーザーは文字種や文字数、記号の細かい選択など柔軟にカスタマイズ可能です。

---

## 技術スタック・構成

- Kotlin Multiplatform Mobile（KMM）
- Kotlin 2.2.0
- Compose Multiplatform（Android/iOS共通UI, Material Design 3 対応）
- Koin（依存性注入）
- kotlinx.coroutines（非同期処理）
- kotlinx.serialization（データシリアライズ）
- ViewModel（状態管理）
- SwiftUI bridge（iOSでCompose UIを表示）

---

## アーキテクチャ

Clean Architecture + MVI
- Domain層：UseCase、Entity、Repository interface
- Data層：Repository実装、DataSource（API・DB・Local）
- Presentation層：各画面ごとにState, Event, Reducer, ViewModel, UIを構成  
  ComposeによってリアクティブなUIを提供

---

## 機能

- **文字数入力**
  - テキスト入力（4文字以上、数字キーボード）

- **生成件数**
  - スライダー（1〜25件）

- **使用文字の選択（チェックボックス）**
  - 英字（大文字）
  - 英字（小文字）
  - 数字
  - 記号（以下の詳細選択あり）

- **記号設定**
  - 一覧から選択（チェックボックス形式）
  - 「すべて選択」チェックボックスあり
    - -(ハイフン)
    - _(アンダーバー)
    - @(アット)
    - /(スラッシュ)
    - *(アスタリスク)
    - +(プラス)
    - ,(カンマ)
    - !(エクスクラメーション)
    - ?(クエスチョン)
    - #(シャープ)
    - $(ドル)
    - %(パーセント)
    - &(アンド)
    - ((左括弧)
    - )(右括弧)
    - {(左中括弧)
    - }(右中括弧)
    - [(左角括弧)
    - ](右角括弧)
    - ~(チルダ)
    - |(パイプ)
    - :(コロン)
    - ;(セミコロン)
    - "(ダブルクォート)
    - '(シングルクォート)
    - ^(キャレット)
    - >(大なり)
    - <(小なり)
    - =(イコール)

  - **記号テキスト入力欄**：任意記号を入力できる。上記記号に存在しない記号が入力された場合でも、パスワード生成で使用する文字として追加される。

- **パスワード生成条件**
  - 「同じ文字列が連続で使われない」チェックボックス：連続する同一文字（例：aa, 11）を含まないよう制御

- **生成処理**
  - 「生成」ボタンで、指定条件に沿った複数パスワードを生成
  - 設定内容はアプリに保存され、次回起動時の初期値として反映

- **生成結果**
  - リスト表示：各パスワードをタップでクリップボードにコピー可能

---

## 画面構成

- 初期画面：文字種や件数など設定UIを表示
- パスワード生成後：Material Design準拠の**Bottom Sheet**にて結果一覧を表示
  - シートは下に隠し、引き上げて再表示可能
- 全体のUIは Material Design 3 に準拠
  - Color Scheme（テーマカラー対応／ダークモード含む）
  - Navigation UI（トップアプリバー、ボトムシート）
  - トグル、スライダー、チェックボックスなどは Material 3 のデザインを使用

---

## テスト方針

- **単体テスト**
  - パスワード生成ロジック（文字種別、記号制限、連続文字制限）
  - 入力バリデーション（文字数制限、記号入力）
  - 設定保存／復元（永続ストレージへの読み書き）

- **UIテスト（可能であれば）**
  - スライダーやチェックボックスの挙動
  - Bottom Sheet の開閉と内容表示確認

---

## ディレクトリ構成（希望）

Clean ArchitectureとMVIに従い、Kotlin Multiplatformのベストプラクティスに基づいた構成を希望します。
  
```
root/
├── shared/
│ ├── domain/
│ │ ├── model/
│ │ ├── usecase/
│ │ └── repository/
│ ├── data/
│ │ ├── repository/ # 実装
│ │ └── datasource/ # API, Local, Memory等
│ ├── presentation/
│ │ └── password_generation/
│ │ ├── PasswordViewModel.kt
│ │ ├── PasswordUiState.kt
│ │ ├── PasswordEvent.kt
│ │ └── PasswordScreen.kt
│ └── core/
│ ├── extension/
│ └── util/
├── androidApp/
│ └── ui/
└── iosApp/
└── swiftui_bridge/
```

---

## その他補足

- 使用記号の選択はアクセシビリティも考慮し、複雑になりすぎないUI構成を希望
- Kotlin MultiplatformはStable直前のバージョンを採用（2.2.0）
- 永続設定保存には `MMKV` または `DataStore` を想定
- UIコンポーネントは Material Design 3 に沿ってデザインする（MaterialTheme3 に準拠）
- Android側では `androidx.compose.material3.*` を使用、iOS側は同等のスタイルが再現されることを想定
